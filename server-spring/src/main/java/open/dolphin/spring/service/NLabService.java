package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Spring Boot service for laboratory test management.
 * Migrated from NLabServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class NLabService {

    private static final Logger logger = Logger.getLogger(NLabService.class.getName());

    // Query constants
    private static final String QUERY_MODULE_BY_MODULE_KEY = "from NLaboModule m where m.moduleKey=:moduleKey";
    private static final String QUERY_MODULE_BY_PID_SAMPLEDATE_LABCODE = "from NLaboModule m where m.patientId=:fidPid and m.sampleDate=:sampleDate and m.laboCenterCode=:laboCode";
    private static final String QUERY_MODULE_BY_FIDPID = "from NLaboModule l where l.patientId=:fidPid order by l.sampleDate desc";
    private static final String QUERY_ITEM_BY_MID = "from NLaboItem l where l.laboModule.id=:mid order by groupCode,parentCode,itemCode";
    private static final String QUERY_ITEM_BY_MID_ORDERBY_SORTKEY = "from NLaboItem l where l.laboModule.id=:mid order by l.sortKey";
    private static final String QUERY_ITEM_BY_FIDPID_ITEMCODE = "from NLaboItem l where l.patientId=:fidPid and l.itemCode=:itemCode order by l.sampleDate desc";
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    private static final String QUERY_MODULECOUNT_BY_FIDPID = "select count(*) from NLaboModule l where l.patientId=:fidPid";

    // Parameter constants
    private static final String PK = "pk";
    private static final String FIDPID = "fidPid";
    private static final String SAMPLEDATE = "sampleDate";
    private static final String LABOCODE = "laboCode";
    private static final String MODULEKEY = "moduleKey";
    private static final String MID = "mid";
    private static final String ITEM_CODE = "itemCode";
    private static final String WOLF = "WOLF";

    @PersistenceContext
    private EntityManager em;

    /**
     * Get constrained patients with lab data.
     * @param facilityId facility ID
     * @param patientIdList list of patient IDs
     * @return list of patient lite models
     */
    public List<PatientLiteModel> getConstrainedPatients(String facilityId, List<String> patientIdList) {
        List<PatientLiteModel> result = new ArrayList<>(patientIdList.size());

        for (String patientId : patientIdList) {
            try {
                PatientModel patient = em.createQuery(
                    "from PatientModel p where p.facilityId=:fid and p.patientId=:pid", PatientModel.class)
                    .setParameter("fid", facilityId)
                    .setParameter("pid", patientId)
                    .getSingleResult();

                result.add(patient.patientAsLiteModel());

            } catch (NoResultException e) {
                // Create dummy patient for unregistered patients
                PatientLiteModel dummy = new PatientLiteModel();
                dummy.setFullName("未登録");
                dummy.setKanaName("未登録");
                dummy.setGender("U");
                result.add(dummy);
            }
        }

        return result;
    }

    /**
     * Create lab test module with proper key management and duplicate handling.
     * @param facilityId facility ID
     * @param module lab module to create
     * @return patient model with health insurance
     */
    public PatientModel create(String facilityId, NLaboModule module) {
        String patientId = module.getPatientId();

        // Get patient by facility ID and patient ID
        PatientModel patient = em.createQuery(
            "from PatientModel p where p.facilityId=:fid and p.patientId=:pid", PatientModel.class)
            .setParameter("fid", facilityId)
            .setParameter("pid", patientId)
            .getSingleResult();

        // Load health insurance if patient exists
        if (patient != null) {
            List<HealthInsuranceModel> insurances = em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                                                     .setParameter(PK, patient.getId())
                                                     .getResultList();
            patient.setHealthInsurances(insurances);
        }

        // Create qualified patient ID
        String qualifiedPatientId = facilityId + ":" + patientId;
        module.setPatientId(qualifiedPatientId);

        // Update item patient IDs
        Collection<NLaboItem> items = module.getItems();
        if (items != null) {
            for (NLaboItem item : items) {
                item.setPatientId(qualifiedPatientId);
            }
        }

        // Handle module key correction
        String sampleDate = module.getSampleDate();
        String laboCode = module.getLaboCenterCode();
        String moduleKey = module.getModuleKey();

        if (moduleKey != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(patientId).append(".").append(sampleDate).append(".").append(laboCode);
            String test = sb.toString();
            if (test.equals(moduleKey)) {
                sb = new StringBuilder();
                sb.append(facilityId);
                sb.append(":");
                sb.append(moduleKey);
                moduleKey = sb.toString();
                module.setModuleKey(moduleKey);
            }
        }

        // Check for existing module and remove if found
        NLaboModule existingModule = findExistingModule(qualifiedPatientId, sampleDate, laboCode, moduleKey);
        if (existingModule != null) {
            em.remove(existingModule);
        }

        // Persist new module
        em.persist(module);

        return patient;
    }

    private NLaboModule findExistingModule(String qualifiedPatientId, String sampleDate,
                                         String laboCode, String moduleKey) {
        try {
            if (moduleKey != null) {
                return em.createQuery(QUERY_MODULE_BY_MODULE_KEY, NLaboModule.class)
                        .setParameter(MODULEKEY, moduleKey)
                        .getSingleResult();
            } else {
                return em.createQuery(QUERY_MODULE_BY_PID_SAMPLEDATE_LABCODE, NLaboModule.class)
                        .setParameter(FIDPID, qualifiedPatientId)
                        .setParameter(SAMPLEDATE, sampleDate)
                        .setParameter(LABOCODE, laboCode)
                        .getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get lab tests for a patient with pagination.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param firstResult first result index
     * @param maxResult maximum number of results
     * @return list of lab modules with items loaded
     */
    public List<NLaboModule> getLaboTest(String qualifiedPatientId, int firstResult, int maxResult) {
        List<NLaboModule> modules = em.createQuery(QUERY_MODULE_BY_FIDPID)
                                    .setParameter(FIDPID, qualifiedPatientId)
                                    .setFirstResult(firstResult)
                                    .setMaxResults(maxResult)
                                    .getResultList();

        // Load items for each module
        for (NLaboModule module : modules) {
            loadModuleItems(module);
        }

        return modules;
    }

    /**
     * Get count of lab tests for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return count of lab test modules
     */
    public Long getLaboTestCount(String qualifiedPatientId) {
        return em.createQuery(QUERY_MODULECOUNT_BY_FIDPID, Long.class)
                .setParameter(FIDPID, qualifiedPatientId)
                .getSingleResult();
    }

    /**
     * Get specific lab test items by item code.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param firstResult first result index
     * @param maxResult maximum number of results
     * @param itemCode lab test item code
     * @return list of lab items
     */
    public List<NLaboItem> getLaboTestItem(String qualifiedPatientId, int firstResult,
                                         int maxResult, String itemCode) {
        return em.createQuery(QUERY_ITEM_BY_FIDPID_ITEMCODE)
                .setParameter(FIDPID, qualifiedPatientId)
                .setParameter(ITEM_CODE, itemCode)
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();
    }

    /**
     * Delete lab test module.
     * @param moduleId module ID to delete
     * @return 1 if successful
     */
    public int deleteLabTest(long moduleId) {
        NLaboModule target = em.find(NLaboModule.class, moduleId);
        if (target != null) {
            em.remove(target);
            logger.info("Lab module deleted: " + moduleId);
            return 1;
        }
        return 0;
    }

    /**
     * Load items for a lab module based on report format.
     * @param module lab module to load items for
     */
    private void loadModuleItems(NLaboModule module) {
        List<NLaboItem> items;

        if (WOLF.equals(module.getReportFormat())) {
            items = em.createQuery(QUERY_ITEM_BY_MID_ORDERBY_SORTKEY)
                     .setParameter(MID, module.getId())
                     .getResultList();
        } else {
            items = em.createQuery(QUERY_ITEM_BY_MID)
                     .setParameter(MID, module.getId())
                     .getResultList();
        }

        module.setItems(items);
    }

    /**
     * Get lab test statistics for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return statistics map
     */
    public java.util.Map<String, Object> getLabTestStats(String qualifiedPatientId) {
        Long count = getLaboTestCount(qualifiedPatientId);
        List<NLaboModule> recentTests = getLaboTest(qualifiedPatientId, 0, 10);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalLabTests", count);
        stats.put("qualifiedPatientId", qualifiedPatientId);
        stats.put("recentTestsCount", recentTests.size());

        // Count by lab center
        java.util.Map<String, Integer> labCenterStats = new java.util.HashMap<>();
        for (NLaboModule test : recentTests) {
            String labCode = test.getLaboCenterCode();
            labCenterStats.put(labCode, labCenterStats.getOrDefault(labCode, 0) + 1);
        }
        stats.put("labCenterStats", labCenterStats);

        return stats;
    }

    /**
     * Get lab test trends for monitoring.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param itemCode specific test item code
     * @param limit number of recent results to return
     * @return list of recent lab items for trend analysis
     */
    public List<NLaboItem> getLabTestTrends(String qualifiedPatientId, String itemCode, int limit) {
        return em.createQuery(QUERY_ITEM_BY_FIDPID_ITEMCODE)
                .setParameter(FIDPID, qualifiedPatientId)
                .setParameter(ITEM_CODE, itemCode)
                .setMaxResults(limit)
                .getResultList();
    }
}
