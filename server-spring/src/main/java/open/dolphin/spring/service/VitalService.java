package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for vital signs management.
 * Migrated from VitalServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class VitalService {

    private static final Logger logger = Logger.getLogger(VitalService.class.getName());

    private static final String QUERY_VITAL_BY_FPID = "from VitalModel v where v.facilityPatId=:fpid";
    private static final String QUERY_VITAL_BY_ID = "from VitalModel v where v.id=:id";

    private static final String ID = "id";
    private static final String FPID = "fpid";

    @PersistenceContext
    private EntityManager em;

    /**
     * Add a new vital sign record.
     * @param vital the vital sign to add
     * @return 1 if successful
     */
    public int addVital(VitalModel vital) {
        try {
            em.persist(vital);
            logger.info("Vital sign added: " + vital.getId());
            return 1;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to add vital sign", e);
            return 0;
        }
    }

    /**
     * Update an existing vital sign record.
     * @param vital the vital sign to update (detached)
     * @return 1 if successful, 0 if not found
     */
    public int updateVital(VitalModel vital) {
        try {
            VitalModel current = em.find(VitalModel.class, vital.getId());
            if (current == null) {
                logger.warning("Vital sign not found for update: " + vital.getId());
                return 0;
            }
            em.merge(vital);
            logger.info("Vital sign updated: " + vital.getId());
            return 1;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update vital sign", e);
            return 0;
        }
    }

    /**
     * Get a vital sign by ID.
     * @param id the vital sign ID
     * @return the vital sign model
     */
    public VitalModel getVital(String id) {
        try {
            VitalModel vital = em.createQuery(QUERY_VITAL_BY_ID, VitalModel.class)
                                .setParameter(ID, Long.parseLong(id))
                                .getSingleResult();
            return vital;
        } catch (NoResultException e) {
            logger.warning("Vital sign not found: " + id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get vital sign: " + id, e);
            return null;
        }
    }

    /**
     * Get all vital signs for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return list of vital signs
     */
    public List<VitalModel> getPatVital(String qualifiedPatientId) {
        try {
            List<VitalModel> vitals = em.createQuery(QUERY_VITAL_BY_FPID)
                                       .setParameter(FPID, qualifiedPatientId)
                                       .getResultList();
            logger.info("Retrieved " + vitals.size() + " vital signs for patient: " + qualifiedPatientId);
            return vitals;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get patient vital signs: " + qualifiedPatientId, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Remove a vital sign by ID.
     * @param id the vital sign ID to remove
     * @return 1 if successful
     */
    public int removeVital(String id) {
        try {
            VitalModel vital = getVital(id);
            if (vital != null) {
                em.remove(vital);
                logger.info("Vital sign removed: " + id);
                return 1;
            } else {
                logger.warning("Vital sign not found for removal: " + id);
                return 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to remove vital sign: " + id, e);
            return 0;
        }
    }

    /**
     * Get patient by qualified patient ID (facilityId:patientId).
     * @param qualifiedPatientId qualified patient ID
     * @return patient model
     */
    public PatientModel getPatientByFpid(String qualifiedPatientId) {
        try {
            String[] parts = qualifiedPatientId.split(":");
            if (parts.length != 2) {
                logger.warning("Invalid qualified patient ID format: " + qualifiedPatientId);
                return null;
            }

            PatientModel patient = em.createQuery(
                "from PatientModel p where p.facilityId=:facilityId and p.patientId=:patientId",
                PatientModel.class)
                .setParameter("facilityId", parts[0])
                .setParameter("patientId", parts[1])
                .getSingleResult();

            return patient;

        } catch (NoResultException e) {
            logger.warning("Patient not found: " + qualifiedPatientId);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get patient: " + qualifiedPatientId, e);
            return null;
        }
    }

    /**
     * Get vital signs statistics for a patient.
     * @param qualifiedPatientId qualified patient ID
     * @return statistics map
     */
    public java.util.Map<String, Object> getVitalStats(String qualifiedPatientId) {
        List<VitalModel> vitals = getPatVital(qualifiedPatientId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalVitals", vitals.size());
        stats.put("qualifiedPatientId", qualifiedPatientId);

        return stats;
    }

    /**
     * Get recent vital signs for monitoring.
     * @param qualifiedPatientId qualified patient ID
     * @param limit maximum number of records to return
     * @return list of recent vital signs
     */
    public List<VitalModel> getRecentVitals(String qualifiedPatientId, int limit) {
        try {
            List<VitalModel> vitals = em.createQuery(QUERY_VITAL_BY_FPID)
                                       .setParameter(FPID, qualifiedPatientId)
                                       .setMaxResults(limit)
                                       .getResultList();
            return vitals;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get recent vitals for: " + qualifiedPatientId, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Check if patient has any vital signs recorded.
     * @param qualifiedPatientId qualified patient ID
     * @return true if patient has vital signs
     */
    public boolean hasVitals(String qualifiedPatientId) {
        try {
            Long count = em.createQuery("select count(v) from VitalModel v where v.facilityPatId=:fpid", Long.class)
                          .setParameter(FPID, qualifiedPatientId)
                          .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to check vitals existence for: " + qualifiedPatientId, e);
            return false;
        }
    }
}
