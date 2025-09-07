package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.*;

/**
 * Spring Boot service for user management and authentication.
 * Migrated from UserServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class UserService {

    private static final String QUERY_USER_BY_UID = "from UserModel u where u.userId=:uid";
    private static final String QUERY_USER_BY_FID_MEMBERTYPE = "from UserModel u where u.userId like :fid and u.memberType!=:memberType";

    private static final String UID = "uid";
    private static final String FID = "fid";
    private static final String MEMBER_TYPE = "memberType";
    private static final String MEMBER_TYPE_EXPIRED = "EXPIRED";

    @PersistenceContext
    private EntityManager em;

    /**
     * Authenticate user with username and password.
     * @param userName username
     * @param password password
     * @return true if authentication successful
     */
    public boolean authenticate(String userName, String password) {
        try {
            UserModel user = (UserModel)
                    em.createQuery(QUERY_USER_BY_UID)
                      .setParameter(UID, userName)
                      .getSingleResult();

            if (user.getPassword() != null && user.getPassword().equals(password)) {
                // Check if user is not expired
                if (user.getMemberType() != null && user.getMemberType().equals(MEMBER_TYPE_EXPIRED)) {
                    return false;
                }
                return true;
            }

        } catch (Exception e) {
            // Authentication failed
        }

        return false;
    }

    /**
     * Add new user to the system.
     * @param user user to add
     * @return 1 if successful
     * @throws EntityExistsException if user already exists
     */
    public int addUser(UserModel user) {
        try {
            // Check if user already exists
            getUser(user.getUserId());
            throw new EntityExistsException("User already exists: " + user.getUserId());
        } catch (NoResultException e) {
            // User doesn't exist, proceed with creation
        }

        em.persist(user);
        return 1;
    }

    /**
     * Get user by user ID.
     * @param userId user ID
     * @return UserModel
     * @throws NoResultException if user not found
     * @throws SecurityException if user is expired
     */
    public UserModel getUser(String userId) {
        UserModel user = (UserModel)
                em.createQuery(QUERY_USER_BY_UID)
                  .setParameter(UID, userId)
                  .getSingleResult();

        if (user.getMemberType() != null && user.getMemberType().equals(MEMBER_TYPE_EXPIRED)) {
            throw new SecurityException("Expired User: " + userId);
        }

        return user;
    }

    /**
     * Get all users for a facility.
     * @param facilityId facility ID
     * @return list of users
     */
    public List<UserModel> getAllUser(String facilityId) {
        List<UserModel> results =
                em.createQuery(QUERY_USER_BY_FID_MEMBERTYPE)
                  .setParameter(FID, facilityId + ":%")
                  .setParameter(MEMBER_TYPE, MEMBER_TYPE_EXPIRED)
                  .getResultList();

        return results;
    }

    /**
     * Update user information.
     * @param user user to update (detached)
     * @return 1 if successful
     */
    public int updateUser(UserModel user) {
        UserModel current = em.find(UserModel.class, user.getId());

        if (current == null) {
            throw new NoResultException("User not found: " + user.getId());
        }

        // Preserve certain fields that shouldn't be updated
        user.setMemberType(current.getMemberType());
        user.setRegisteredDate(current.getRegisteredDate());

        em.merge(user);
        return 1;
    }

    /**
     * Remove user from the system.
     * @param userId user ID to remove
     * @return 1 if successful
     */
    public int removeUser(String userId) {
        UserModel userToRemove = getUser(userId);

        // Cascade delete related entities
        deleteUserRelatedEntities(userToRemove);

        // Handle doctor vs regular user deletion
        if (userToRemove.getLicenseModel() != null &&
            "doctor".equals(userToRemove.getLicenseModel().getLicense())) {
            // For doctors, mark as expired instead of deleting
            userToRemove.setMemberType(MEMBER_TYPE_EXPIRED);
            userToRemove.setPassword("c9dbeb1de83e60eb1eb3675fa7d69a02"); // Expired password
            em.merge(userToRemove);
        } else {
            // For regular users, delete completely
            em.remove(userToRemove);
        }

        return 1;
    }

    /**
     * Delete all entities related to a user.
     * @param user user whose related entities should be deleted
     */
    private void deleteUserRelatedEntities(UserModel user) {
        Long userId = user.getId();

        // Delete stamps
        List<StampModel> stamps = em.createQuery("from StampModel s where s.userId = :pk")
                                   .setParameter("pk", userId)
                                   .getResultList();
        stamps.forEach(em::remove);

        // Delete subscribed trees
        List<SubscribedTreeModel> subscribedTrees =
                em.createQuery("from SubscribedTreeModel s where s.user.id = :pk")
                  .setParameter("pk", userId)
                  .getResultList();
        subscribedTrees.forEach(em::remove);

        // Delete published trees
        List<PublishedTreeModel> publishedTrees =
                em.createQuery("from PublishedTreeModel p where p.user.id = :pk")
                  .setParameter("pk", userId)
                  .getResultList();
        publishedTrees.forEach(em::remove);

        // Delete stamp trees
        List<StampTreeModel> stampTrees =
                em.createQuery("from StampTreeModel s where s.user.id = :pk")
                  .setParameter("pk", userId)
                  .getResultList();
        stampTrees.forEach(em::remove);
    }

    /**
     * Update facility information.
     * @param user user containing facility information to update
     * @return 1 if successful
     */
    public int updateFacility(UserModel user) {
        FacilityModel updateFacility = user.getFacilityModel();
        FacilityModel current = em.find(FacilityModel.class, updateFacility.getId());

        if (current == null) {
            throw new NoResultException("Facility not found: " + updateFacility.getId());
        }

        // Preserve certain fields
        updateFacility.setMemberType(current.getMemberType());
        updateFacility.setRegisteredDate(current.getRegisteredDate());

        em.merge(updateFacility);
        return 1;
    }

    /**
     * Get current authenticated user from Spring Security context.
     * @return current user ID or null if not authenticated
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Check if current user has a specific role.
     * @param role role to check
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
}
