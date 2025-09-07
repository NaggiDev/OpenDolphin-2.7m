package open.dolphin.spring.service;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.session.AccountSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for system operations and facility management.
 * Migrated from SystemServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class SystemService {

    private static final Logger logger = Logger.getLogger(SystemService.class.getName());

    private static final String BASE_OID = "1.3.6.1.4.1.9414.72.";
    private static final String DEMO_FACILITY_ID = "1.3.6.1.4.1.9414.70.1";

    private static final String QUERY_NEXT_FID = "select nextval('facility_num') as n";
    private static final String QUERY_FACILITY_BY_FID = "from FacilityModel f where f.facilityId=:fid";
    private static final String FID = "fid";
    private static final String PK = "pk";

    private static final String ASP_TESTER = "ASP_TESTER";
    private static final int MAX_DEMO_PATIENTS = 5;
    private static final String ID_PREFIX = "D_";
    private static final String QUERY_PATIENT_BY_FID = "from PatientModel p where p.facilityId=:fid order by p.patientId";
    private static final String QUERY_HEALTH_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    private static final String TREE_SOURCE = "1.3.6.1.4.1.9414.70.1:admin";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OidSenderService oidSenderService;

    /**
     * Add facility and admin user.
     * Creates facility, admin user, demo patients, and stamp trees.
     * @param user admin user for the facility
     * @return account summary
     */
    public AccountSummary addFacilityAdmin(UserModel user) {
        // Get next facility number from sequence
        java.math.BigInteger nextId = (java.math.BigInteger) em.createNativeQuery(QUERY_NEXT_FID).getSingleResult();
        Long nextFnum = nextId.longValue();

        // Generate facility OID
        String fid = BASE_OID + nextFnum;

        // Check if facility already exists
        try {
            em.createQuery(QUERY_FACILITY_BY_FID)
               .setParameter(FID, fid)
               .getSingleResult();
            throw new EntityExistsException("Facility already exists: " + fid);
        } catch (NoResultException e) {
            // Expected - facility doesn't exist
        }

        // Set facility ID and persist
        FacilityModel facility = user.getFacilityModel();
        facility.setFacilityId(fid);
        em.persist(facility);

        // Set user ID as fid:uid
        user.setUserId(fid + IInfoModel.COMPOSITE_KEY_MAKER + user.getUserId());

        // Set up roles
        Collection<RoleModel> roles = user.getRoles();
        if (roles != null) {
            for (RoleModel role : roles) {
                role.setUserModel(user);
                role.setUserId(user.getUserId());
            }
        }

        // Persist user
        em.persist(user);

        // Create demo patients
        createDemoPatients(fid);

        // Create stamp tree
        createStampTree(user);

        // Return account summary
        AccountSummary account = new AccountSummary();
        account.setMemberType(ASP_TESTER);
        account.setFacilityAddress(facility.getAddress());
        account.setFacilityId(facility.getFacilityId());
        account.setFacilityName(facility.getFacilityName());
        account.setFacilityTelephone(facility.getTelephone());
        account.setFacilityZipCode(facility.getZipCode());
        account.setUserEmail(user.getEmail());
        account.setUserName(user.getCommonName());
        account.setUserId(user.idAsLocal());

        return account;
    }

    private void createDemoPatients(String fid) {
        List<PatientModel> demoPatients = em.createQuery(QUERY_PATIENT_BY_FID)
                                           .setParameter(FID, DEMO_FACILITY_ID)
                                           .setFirstResult(1)
                                           .setMaxResults(MAX_DEMO_PATIENTS)
                                           .getResultList();

        for (PatientModel demoPatient : demoPatients) {
            PatientModel copyPatient = new PatientModel();
            copyPatient.setFacilityId(fid);
            copyPatient.setPatientId(ID_PREFIX + demoPatient.getPatientId());
            copyPatient.setFamilyName(demoPatient.getFamilyName());
            copyPatient.setGivenName(demoPatient.getGivenName());
            copyPatient.setFullName(demoPatient.getFullName());
            copyPatient.setKanaFamilyName(demoPatient.getKanaFamilyName());
            copyPatient.setKanaGivenName(demoPatient.getKanaGivenName());
            copyPatient.setKanaName(demoPatient.getKanaName());
            copyPatient.setGender(demoPatient.getGender());
            copyPatient.setGenderDesc(demoPatient.getGenderDesc());
            copyPatient.setBirthday(demoPatient.getBirthday());
            copyPatient.setSimpleAddressModel(demoPatient.getSimpleAddressModel());
            copyPatient.setTelephone(demoPatient.getTelephone());

            // Copy health insurance
            List<HealthInsuranceModel> demoInsurances = em.createQuery(QUERY_HEALTH_INSURANCE_BY_PATIENT_PK)
                                                         .setParameter(PK, demoPatient.getId())
                                                         .getResultList();

            for (HealthInsuranceModel demoInsurance : demoInsurances) {
                HealthInsuranceModel copyInsurance = new HealthInsuranceModel();
                copyInsurance.setBeanBytes(demoInsurance.getBeanBytes());
                copyInsurance.setPatient(copyPatient);
                copyPatient.addHealthInsurance(copyInsurance);
            }

            em.persist(copyPatient);

            // Create karte
            KarteBean karte = new KarteBean();
            karte.setPatientModel(copyPatient);
            karte.setCreated(new Date());
            em.persist(karte);
        }
    }

    private void createStampTree(UserModel user) {
        try {
            // Get admin's stamp tree
            UserModel admin = (UserModel) em.createQuery("from UserModel u where u.userId=:uid")
                                           .setParameter("uid", TREE_SOURCE)
                                           .getSingleResult();

            List<StampTreeModel> trees = em.createQuery("from StampTreeModel s where s.user.id=:userPK")
                                          .setParameter("userPK", admin.getId())
                                          .getResultList();

            if (!trees.isEmpty()) {
                StampTreeModel sourceTree = trees.get(0);

                // Copy tree XML
                InputStream is = new ByteArrayInputStream(sourceTree.getTreeBytes());
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                // TODO: Implement CopyStampTreeBuilder and Director
                // For now, create a basic stamp tree
                String basicTreeXml = createBasicStampTreeXml();
                byte[] treeBytes = basicTreeXml.getBytes("UTF-8");

                // Create and persist copy tree
                StampTreeModel copyTree = new StampTreeModel();
                copyTree.setTreeBytes(treeBytes);
                copyTree.setUserModel(user);
                copyTree.setName("個人用");
                copyTree.setDescription("個人用のスタンプセットです");
                copyTree.setPartyName(user.getFacilityModel().getFacilityName());

                if (user.getFacilityModel().getUrl() != null) {
                    copyTree.setUrl(user.getFacilityModel().getUrl());
                }

                em.persist(copyTree);

                br.close();
            }

        } catch (Exception e) {
            logger.warning("Failed to create stamp tree: " + e.getMessage());
        }
    }

    private String createBasicStampTreeXml() {
        // TODO: Implement proper stamp tree XML generation
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><stampTree><root/></stampTree>";
    }

    /**
     * Count total activities for a facility.
     * @param fid facility ID
     * @return activity model with counts
     */
    public ActivityModel countTotalActivities(String fid) {
        ActivityModel am = new ActivityModel();

        // Count users
        Long userCount = (Long) em.createQuery(
                "select count(u.id) from UserModel u where u.userId like :fid and u.memberType!=:memberType")
                .setParameter("fid", fid + ":%")
                .setParameter("memberType", "EXPIRED")
                .getSingleResult();
        am.setNumOfUsers(userCount);

        // Count patients
        Long patientCount = (Long) em.createQuery(
                "select count(p.id) from PatientModel p where p.facilityId=:fid")
                .setParameter("fid", fid)
                .getSingleResult();
        am.setNumOfPatients(patientCount);

        // Count patient visits
        Long visitCount = (Long) em.createQuery(
                "select count(p.id) from PatientVisitModel p where p.facilityId=:fid and p.status!=:status")
                .setParameter("fid", fid)
                .setParameter("status", 6)
                .getSingleResult();
        am.setNumOfPatientVisits(visitCount);

        // Count karte documents
        Long karteCount = (Long) em.createQuery(
                "select count(d.id) from DocumentModel d where d.creator.userId like :fid and d.status='F'")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfKarte(karteCount);

        // Count images
        Long imageCount = (Long) em.createQuery(
                "select count(s.id) from SchemaModel s where s.creator.userId like :fid and s.status='F'")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfImages(imageCount);

        // Count attachments
        Long attachmentCount = (Long) em.createQuery(
                "select count(a.id) from AttachmentModel a where a.creator.userId like :fid and a.status='F'")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfAttachments(attachmentCount);

        // Count diagnoses
        Long diagnosisCount = (Long) em.createQuery(
                "select count(r.id) from RegisteredDiagnosisModel r where r.creator.userId like :fid")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfDiagnosis(diagnosisCount);

        // Count letters
        Long letterCount = (Long) em.createQuery(
                "select count(l.id) from LetterModule l where l.creator.userId like :fid and l.status='F'")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfLetters(letterCount);

        // Count lab tests
        Long labTestCount = (Long) em.createQuery(
                "select count(l.id) from NLaboModule l where l.patientId like :fid")
                .setParameter("fid", fid + ":%")
                .getSingleResult();
        am.setNumOfLabTests(labTestCount);

        // Get facility information
        FacilityModel facility = (FacilityModel) em.createQuery(
                "from FacilityModel f where f.facilityId=:fid")
                .setParameter("fid", fid)
                .getSingleResult();

        am.setFacilityId(facility.getFacilityId());
        am.setFacilityName(facility.getFacilityName());
        am.setFacilityZip(facility.getZipCode());
        am.setFacilityAddress(facility.getAddress());
        am.setFacilityTelephone(facility.getTelephone());
        am.setFacilityFacimile(facility.getFacsimile());

        // Get database size
        try {
            String dbSize = (String) em.createNativeQuery("select pg_size_pretty(pg_database_size('dolphin'))")
                                      .getSingleResult();
            am.setDbSize(dbSize);
        } catch (Exception e) {
            am.setDbSize("Unknown");
        }

        // Get bind address
        am.setBindAddress(getBindAddress());

        return am;
    }

    /**
     * Count activities within a date range.
     * @param fid facility ID
     * @param from start date
     * @param to end date
     * @return activity model with counts
     */
    public ActivityModel countActivities(String fid, Date from, Date to) {
        ActivityModel am = new ActivityModel();
        am.setFromDate(from);
        am.setToDate(to);

        // Count new patients in period
        Long patientCount = (Long) em.createQuery(
                "select count(p.id) from PatientModel p, KarteBean k where p.id=k.patient.id and p.facilityId=:fid and k.created between :fromDate and :toDate")
                .setParameter("fid", fid)
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfPatients(patientCount);

        // Count visits in period
        Long visitCount = (Long) em.createQuery(
                "select count(p.id) from PatientVisitModel p where p.facilityId=:fid and p.pvtDate between :fromDate and :toDate and p.status!=:status")
                .setParameter("fid", fid)
                .setParameter("fromDate", pvtDateFromDate(from))
                .setParameter("toDate", pvtDateFromDate(to))
                .setParameter("status", 6)
                .getSingleResult();
        am.setNumOfPatientVisits(visitCount);

        // Count karte documents in period
        Long karteCount = (Long) em.createQuery(
                "select count(d.id) from DocumentModel d where d.creator.userId like :fid and d.started between :fromDate and :toDate and d.status='F'")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfKarte(karteCount);

        // Count images in period
        Long imageCount = (Long) em.createQuery(
                "select count(s.id) from SchemaModel s where s.creator.userId like :fid and s.started between :fromDate and :toDate and s.status='F'")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfImages(imageCount);

        // Count attachments in period
        Long attachmentCount = (Long) em.createQuery(
                "select count(a.id) from AttachmentModel a where a.creator.userId like :fid and a.started between :fromDate and :toDate and a.status='F'")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfAttachments(attachmentCount);

        // Count diagnoses in period
        Long diagnosisCount = (Long) em.createQuery(
                "select count(r.id) from RegisteredDiagnosisModel r where r.creator.userId like :fid and r.started between :fromDate and :toDate")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfDiagnosis(diagnosisCount);

        // Count letters in period
        Long letterCount = (Long) em.createQuery(
                "select count(l.id) from LetterModule l where l.creator.userId like :fid and l.started between :fromDate and :toDate and l.status='F'")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        am.setNumOfLetters(letterCount);

        // Count lab tests in period
        Long labTestCount = (Long) em.createQuery(
                "select count(l.id) from NLaboModule l where l.patientId like :fid and l.sampleDate between :fromDate and :toDate")
                .setParameter("fid", fid + ":%")
                .setParameter("fromDate", sampleDateFromDate(from))
                .setParameter("toDate", sampleDateFromDate(to))
                .getSingleResult();
        am.setNumOfLabTests(labTestCount);

        return am;
    }

    /**
     * Send activity reports.
     * @param activities activity models to report
     */
    public void mailActivities(ActivityModel[] activities) {
        ActivityModel am = activities[0];
        ActivityModel total = activities[1];

        // Log activity information
        logActivityInfo(am, total);

        // Send activity report
        logger.info("Sending activity report...");
        try {
            oidSenderService.sendActivity(activities);
        } catch (Exception e) {
            logger.warning("Failed to send activity report: " + e.getMessage());
        }
    }

    private void logActivityInfo(ActivityModel am, ActivityModel total) {
        logger.info("開始日時=" + am.getFromDate());
        logger.info("終了日時=" + am.getToDate());
        logger.info("医療機関ID=" + total.getFacilityId());
        logger.info("医療機関名=" + total.getFacilityName());
        logger.info("郵便番号=" + total.getFacilityZip());
        logger.info("住所=" + total.getFacilityAddress());
        logger.info("電話=" + total.getFacilityTelephone());
        logger.info("FAX=" + total.getFacilityFacimile());
        logger.info("利用者数=" + am.getNumOfUsers());
        logger.info("患者数=" + am.getNumOfPatients() + "/" + total.getNumOfPatients());
        logger.info("来院数=" + am.getNumOfPatientVisits() + "/" + total.getNumOfPatientVisits());
        logger.info("病名数=" + am.getNumOfDiagnosis() + "/" + total.getNumOfDiagnosis());
        logger.info("カルテ枚数=" + am.getNumOfKarte() + "/" + total.getNumOfKarte());
        logger.info("画像数=" + am.getNumOfImages() + "/" + total.getNumOfImages());
        logger.info("添付文書数=" + am.getNumOfAttachments() + "/" + total.getNumOfAttachments());
        logger.info("紹介状数=" + am.getNumOfLetters() + "/" + total.getNumOfLetters());
        logger.info("検査数=" + am.getNumOfLabTests() + "/" + total.getNumOfLabTests());
        logger.info("データベース容量=" + total.getDbSize());
        logger.info("IP アドレス=" + total.getBindAddress());
    }

    /**
     * Send monthly activity reports for all facilities.
     * @param year year
     * @param month month (0-based)
     */
    public void sendMonthlyActivities(int year, int month) {
        // Calculate date range for the month
        GregorianCalendar gcFrom = new GregorianCalendar(year, month, 1);
        Date fromDate = gcFrom.getTime();

        GregorianCalendar gcTo = new GregorianCalendar(year, month,
                gcFrom.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date toDate = gcTo.getTime();

        // Get all facilities
        List<FacilityModel> facilities = em.createQuery("from FacilityModel f").getResultList();

        for (FacilityModel facility : facilities) {
            try {
                // Count total activities
                ActivityModel total = countTotalActivities(facility.getFacilityId());
                total.setFlag("T");

                // Count activities for the month
                ActivityModel monthly = countActivities(facility.getFacilityId(), fromDate, toDate);
                monthly.setFlag("M");
                monthly.setFromDate(fromDate);
                monthly.setToDate(toDate);

                // Send report
                mailActivities(new ActivityModel[]{monthly, total});

            } catch (Exception e) {
                logger.warning("Failed to send monthly activities for facility " +
                              facility.getFacilityId() + ": " + e.getMessage());
            }
        }
    }

    private String pvtDateFromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
    }

    private String sampleDateFromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private String getBindAddress() {
        String address = System.getProperty("jboss.bind.address");
        if (address == null) {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                if (ip != null) {
                    address = ip.toString();
                }
            } catch (UnknownHostException e) {
                logger.log(Level.SEVERE, "Failed to get bind address", e);
            }
        }
        return address;
    }
}
