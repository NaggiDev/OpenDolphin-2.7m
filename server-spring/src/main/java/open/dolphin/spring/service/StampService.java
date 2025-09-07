package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.*;

/**
 * Spring Boot service for stamp tree and stamp management.
 * Migrated from StampServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class StampService {

    private static final String QUERY_TREE_BY_USER_PK = "from StampTreeModel s where s.user.id=:userPK";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK = "from SubscribedTreeModel s where s.user.id=:userPK";
    private static final String QUERY_LOCAL_PUBLISHED_TREE = "from PublishedTreeModel p where p.publishType=:fid";
    private static final String QUERY_PUBLIC_TREE = "from PublishedTreeModel p where p.publishType='global'";
    private static final String QUERY_PUBLISHED_TREE_BY_ID = "from PublishedTreeModel p where p.id=:id";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK_TREE_ID = "from SubscribedTreeModel s where s.user.id=:userPK and s.treeId=:treeId";
    private static final String EXCEPTION_FIRST_COMMIT_WIN = "First Commit Win Exception";

    private static final String USER_PK = "userPK";
    private static final String FID = "fid";
    private static final String TREE_ID = "treeId";
    private static final String ID = "id";

    @PersistenceContext
    private EntityManager em;

    /**
     * Calculate next version number for optimistic locking.
     * @param holdVersion version held by client
     * @param dbVersion version in database
     * @return next version number or -1 if conflict
     */
    private int getNextVersion(String holdVersion, String dbVersion) {
        int newVersion = -1;

        if (holdVersion != null && dbVersion != null) {
            // First commit wins - versions must match
            if (holdVersion.equals(dbVersion)) {
                newVersion = Integer.parseInt(holdVersion) + 1;
            }
        } else if (holdVersion == null && dbVersion != null) {
            // Should not happen
        } else if (holdVersion != null && dbVersion == null) {
            // Should not happen
        } else if (holdVersion == null && dbVersion == null) {
            // Both don't exist - new tree being saved
            newVersion = 0;
        }

        return newVersion;
    }

    /**
     * Save/update user's personal stamp tree.
     * @param model stamp tree to save
     * @return saved tree ID
     */
    public long putTree(StampTreeModel model) {
        int version;

        try {
            StampTreeModel exist = (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, model.getUserModel().getId())
                      .getSingleResult();

            version = getNextVersion(model.getVersionNumber(), exist.getVersionNumber());

        } catch (NoResultException e) {
            version = 0;
        }

        if (version >= 0) {
            model.setVersionNumber(String.valueOf(version));
            StampTreeModel saveOrUpdate = em.merge(model);
            return saveOrUpdate.getId();
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * Synchronize stamp tree with versioning.
     * @param model stamp tree to sync
     * @return "id,versionNumber"
     */
    public String syncTree(StampTreeModel model) {
        int version;

        try {
            StampTreeModel exist = (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, model.getUserModel().getId())
                      .getSingleResult();

            version = getNextVersion(model.getVersionNumber(), exist.getVersionNumber());

        } catch (NoResultException e) {
            version = 0;
        }

        if (version >= 0) {
            model.setVersionNumber(String.valueOf(version));
            StampTreeModel saveOrUpdate = em.merge(model);
            return saveOrUpdate.getId() + "," + saveOrUpdate.getVersionNumber();
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * Force synchronize stamp tree (bypass versioning).
     * @param model stamp tree to force sync
     */
    public void forceSyncTree(StampTreeModel model) {
        em.merge(model);
    }

    /**
     * Get user's personal and subscribed stamp trees.
     * @param userPK user primary key
     * @return stamp tree holder with personal and subscribed trees
     */
    public StampTreeHolder getTrees(long userPK) {
        StampTreeHolder ret = new StampTreeHolder();

        // Get personal tree
        List<StampTreeModel> list = em.createQuery(QUERY_TREE_BY_USER_PK)
                                      .setParameter(USER_PK, userPK)
                                      .getResultList();

        // New user case
        if (list.isEmpty()) {
            return ret;
        }

        // Add first tree as personal tree
        StampTreeModel st = list.remove(0);
        ret.setPersonalTree(st);

        // Remove any additional trees (BUG scenario)
        if (!list.isEmpty()) {
            for (StampTreeModel extraTree : list) {
                em.remove(extraTree);
            }
        }

        // Get subscribed trees
        List<SubscribedTreeModel> subscribed = em.createQuery(QUERY_SUBSCRIBED_BY_USER_PK)
                                                 .setParameter(USER_PK, userPK)
                                                 .getResultList();

        Map<Long, String> tmp = new HashMap<>(5, 0.8f);

        for (SubscribedTreeModel sm : subscribed) {
            // Check for duplicates
            if (tmp.get(sm.getTreeId()) == null) {
                tmp.put(sm.getTreeId(), "A");

                try {
                    PublishedTreeModel published = em.find(PublishedTreeModel.class, sm.getTreeId());
                    if (published != null) {
                        ret.addSubscribedTree(published);
                    } else {
                        em.remove(sm);
                    }
                } catch (NoResultException e) {
                    em.remove(sm);
                }
            } else {
                // Remove duplicate subscriptions
                em.remove(sm);
            }
        }

        return ret;
    }

    /**
     * Update published tree with personal tree sync.
     * @param holder stamp tree holder
     * @return version number
     */
    public String updatePublishedTree(StampTreeHolder holder) {
        StampTreeModel personalTree = holder.getPersonalTree();
        PublishedTreeModel publishedTree = holder.getSubscribedList().get(0);

        // Personal tree must sync first
        int version;
        try {
            StampTreeModel exist = (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, personalTree.getUserModel().getId())
                      .getSingleResult();

            version = getNextVersion(personalTree.getVersionNumber(), exist.getVersionNumber());

        } catch (NoResultException e) {
            version = 0;
        }

        if (version >= 0) {
            personalTree.setVersionNumber(String.valueOf(version));
            StampTreeModel saveOrUpdate = em.merge(personalTree);

            if (publishedTree.getId() == 0L) {
                publishedTree.setId(personalTree.getId());
                em.persist(publishedTree);
            } else {
                em.merge(publishedTree);
            }

            return saveOrUpdate.getVersionNumber();
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * Cancel published tree.
     * @param stampTree stamp tree to cancel publishing
     * @return version number
     */
    public String cancelPublishedTree(StampTreeModel stampTree) {
        // Personal tree must sync first
        int version;
        try {
            StampTreeModel exist = (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, stampTree.getUserModel().getId())
                      .getSingleResult();

            version = getNextVersion(stampTree.getVersionNumber(), exist.getVersionNumber());

        } catch (NoResultException e) {
            version = 0;
        }

        if (version >= 0) {
            stampTree.setVersionNumber(String.valueOf(version));
            StampTreeModel saveOrUpdate = em.merge(stampTree);

            // Remove published tree
            List<PublishedTreeModel> publishedTrees = em.createQuery(QUERY_PUBLISHED_TREE_BY_ID)
                                                        .setParameter(ID, stampTree.getId())
                                                        .getResultList();
            for (PublishedTreeModel published : publishedTrees) {
                em.remove(published);
            }

            return saveOrUpdate.getVersionNumber();
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * Get published stamp trees (local and global).
     * @param facilityId facility ID
     * @return list of published trees
     */
    public List<PublishedTreeModel> getPublishedTrees(String facilityId) {
        List<PublishedTreeModel> ret = new ArrayList<>();

        // Get local published trees
        List<PublishedTreeModel> locals = em.createQuery(QUERY_LOCAL_PUBLISHED_TREE)
                                           .setParameter(FID, facilityId)
                                           .getResultList();
        ret.addAll(locals);

        // Get public trees
        List<PublishedTreeModel> publics = em.createQuery(QUERY_PUBLIC_TREE)
                                            .getResultList();
        ret.addAll(publics);

        return ret;
    }

    /**
     * Subscribe to published trees.
     * @param subscriptions list of subscriptions to add
     * @return list of subscription IDs
     */
    public List<Long> subscribeTrees(List<SubscribedTreeModel> subscriptions) {
        List<Long> ret = new ArrayList<>();
        for (SubscribedTreeModel subscription : subscriptions) {
            em.persist(subscription);
            ret.add(subscription.getId());
        }
        return ret;
    }

    /**
     * Unsubscribe from published trees.
     * @param treeUserPairs list of [treeId, userPK] pairs
     * @return number of unsubscriptions
     */
    public int unsubscribeTrees(List<Long> treeUserPairs) {
        int count = 0;
        int len = treeUserPairs.size();

        for (int i = 0; i < len; i += 2) {
            Long treeId = treeUserPairs.get(i);
            Long userPK = treeUserPairs.get(i + 1);

            List<SubscribedTreeModel> subscriptions = em.createQuery(QUERY_SUBSCRIBED_BY_USER_PK_TREE_ID)
                                                       .setParameter(USER_PK, userPK)
                                                       .setParameter(TREE_ID, treeId)
                                                       .getResultList();

            for (SubscribedTreeModel subscription : subscriptions) {
                em.remove(subscription);
            }
            count++;
        }
        return count;
    }

    /**
     * Save stamps.
     * @param stamps list of stamps to save
     * @return list of stamp IDs
     */
    public List<String> putStamp(List<StampModel> stamps) {
        List<String> ret = new ArrayList<>();
        for (StampModel stamp : stamps) {
            em.persist(stamp);
            ret.add(stamp.getId());
        }
        return ret;
    }

    /**
     * Save single stamp.
     * @param stamp stamp to save
     * @return stamp ID
     */
    public String putStamp(StampModel stamp) {
        StampModel saved = em.merge(stamp);
        return saved.getId();
    }

    /**
     * Get stamp by ID.
     * @param stampId stamp ID
     * @return stamp model or null if not found
     */
    public StampModel getStamp(String stampId) {
        try {
            return em.find(StampModel.class, stampId);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get stamps by IDs.
     * @param stampIds list of stamp IDs
     * @return list of stamp models
     */
    public List<StampModel> getStamp(List<String> stampIds) {
        List<StampModel> ret = new ArrayList<>();
        try {
            for (String stampId : stampIds) {
                StampModel stamp = em.find(StampModel.class, stampId);
                if (stamp != null) {
                    ret.add(stamp);
                }
            }
        } catch (Exception e) {
            // Handle exceptions gracefully
        }
        return ret;
    }

    /**
     * Remove stamp by ID.
     * @param stampId stamp ID to remove
     * @return 1 if successful
     */
    public int removeStamp(String stampId) {
        StampModel stamp = em.find(StampModel.class, stampId);
        if (stamp != null) {
            em.remove(stamp);
            return 1;
        }
        return 0;
    }

    /**
     * Remove stamps by IDs.
     * @param stampIds list of stamp IDs to remove
     * @return number of stamps removed
     */
    public int removeStamp(List<String> stampIds) {
        int count = 0;
        for (String stampId : stampIds) {
            StampModel stamp = em.find(StampModel.class, stampId);
            if (stamp != null) {
                em.remove(stamp);
                count++;
            }
        }
        return count;
    }
}
