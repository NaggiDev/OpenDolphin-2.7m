package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import open.dolphin.spring.service.ClaimSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for schedule management and patient visit operations.
 * Migrated from ScheduleServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class ScheduleService {

    private static final Logger logger = Logger.getLogger(ScheduleService.class.getName());

    private static final String QUERY_PVT_BY_FID_DATE
            = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date order by p.pvtDate";

    private static final String QUERY_PVT_BY_FID_DID_DATE
            = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";

    private static final String QUERY_INSURANCE_BY_PATIENT_ID
            = "from HealthInsuranceModel h where h.patient.id=:id";

    private static final String QUERY_KARTE
            = "from KarteBean k where k.patient.id=:patientPk";

    private static final String QUERY_LASTDOC_DATE_BY_KARTEID_FINAL
            = "select max(m.started) from d_document m where m.karte_id=:karteId and m.docType=:docType and (m.status = 'F' or m.status = 'T')";

    private static final String QUERY_DOCUMENT_BY_KARTEID_STARTDATE
            = "from DocumentModel d where d.karte.id=:karteId and d.started=:started and (d.status='F' or d.status='T')";

    private static final String QUERY_DOCUMENT_BY_LINK_ID
            = "from DocumentModel d where d.linkId=:id";

    private static final String QUERY_MODULE_BY_DOC_ID
            = "from ModuleModel m where m.document.id=:id";

    private static final String QUERY_SCHEMA_BY_DOC_ID
            = "from SchemaModel i where i.document.id=:id";

    private static final String QUERY_ATTACHMENT_BY_DOC_ID
            = "from AttachmentModel a where a.document.id=:id";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ClaimSenderService claimSenderService;

    /**
     * Get patient visit information with health insurance and karte data.
     * @param facilityId facility ID
     * @param doctorId doctor ID (optional)
     * @param unassigned unassigned doctor ID (optional)
     * @param date date string (yyyy-MM-dd)
     * @return list of patient visits
     */
    public List<PatientVisitModel> getPvt(String facilityId, String doctorId, String unassigned, String date) {
        List<PatientVisitModel> result;

        if (doctorId == null && unassigned == null) {
            result = em.createQuery(QUERY_PVT_BY_FID_DATE)
                      .setParameter("fid", facilityId)
                      .setParameter("date", date + "%")
                      .getResultList();
        } else {
            result = em.createQuery(QUERY_PVT_BY_FID_DID_DATE)
                      .setParameter("fid", facilityId)
                      .setParameter("did", doctorId)
                      .setParameter("unassigned", unassigned)
                      .setParameter("date", date + "%")
                      .getResultList();
        }

        if (result.isEmpty()) {
            return result;
        }

        // Convert date string to Date
        Date startDate = dateFromString(date);

        // Load patient health insurance and karte information
        for (PatientVisitModel pvt : result) {
            PatientModel patient = pvt.getPatientModel();

            // Load health insurance
            List<HealthInsuranceModel> insurances = em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                                                     .setParameter("id", patient.getId())
                                                     .getResultList();
            patient.setHealthInsurances(insurances);

            // Load karte
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                      .setParameter("patientPk", patient.getId())
                                      .getResultList();

            if (!kartes.isEmpty()) {
                KarteBean karte = kartes.get(0);

                // Check if karte exists for this date
                List<DocumentModel> documents = em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                .setParameter("karteId", karte.getId())
                                                .setParameter("started", startDate)
                                                .getResultList();

                if (!documents.isEmpty()) {
                    pvt.setLastDocDate(startDate);
                }
            }
        }

        return result;
    }

    /**
     * Create scheduled medical document and optionally send claim.
     * @param pvtPK patient visit primary key
     * @param userPK user primary key
     * @param startDate appointment date
     * @param send whether to send claim
     * @return 1 if successful, 0 otherwise
     */
    public int makeScheduleAndSend(long pvtPK, long userPK, Date startDate, boolean send) {
        try {
            // Get patient visit
            PatientVisitModel pvt = em.find(PatientVisitModel.class, pvtPK);
            PatientModel patient = pvt.getPatientModel();

            // Load health insurance
            List<HealthInsuranceModel> insurances = em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                                                     .setParameter("id", patient.getId())
                                                     .getResultList();
            patient.setHealthInsurances(insurances);

            // Decode health insurance
            PVTHealthInsuranceModel pvtHealthInsurance = null;
            for (HealthInsuranceModel insurance : insurances) {
                XMLDecoder decoder = new XMLDecoder(
                    new BufferedInputStream(
                        new ByteArrayInputStream(insurance.getBeanBytes())));
                pvtHealthInsurance = (PVTHealthInsuranceModel) decoder.readObject();
                break;
            }

            // Get user
            UserModel user = em.find(UserModel.class, userPK);

            // Get karte
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                      .setParameter("patientPk", patient.getId())
                                      .getResultList();
            KarteBean karte = kartes.get(0);

            // Check if document already exists for this date
            List<DocumentModel> existingDocuments = em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                     .setParameter("karteId", karte.getId())
                                                     .setParameter("started", startDate)
                                                     .getResultList();

            if (!existingDocuments.isEmpty()) {
                logger.log(Level.INFO, "{0} has karte at {1}",
                          new Object[]{patient.getFullName(), startDate});
                return 0;
            }

            // Create scheduled document
            DocumentModel schedule = createScheduledDocument(karte, user, startDate);

            // Set document info
            setDocumentInfo(schedule, pvt, user, patient, pvtHealthInsurance, karte);

            // Set relationships
            setupDocumentRelationships(schedule);

            // Set claim sending flag
            send = send && (schedule.getModules() != null && !schedule.getModules().isEmpty());
            schedule.getDocInfoModel().setSendClaim(send);

            // Persist document
            em.persist(schedule);

            // Send claim if requested
            if (send) {
                claimSenderService.sendDocument(schedule);
            }

            return 1;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create scheduled document", e);
        }

        return 0;
    }

    private DocumentModel createScheduledDocument(KarteBean karte, UserModel user, Date startDate) {
        try {
            // Get latest document date
            Date lastDocDate = (Date) em.createNativeQuery(QUERY_LASTDOC_DATE_BY_KARTEID_FINAL)
                                       .setParameter("karteId", karte.getId())
                                       .setParameter("docType", "karte")
                                       .getSingleResult();

            // Get latest document
            List<DocumentModel> latestDocuments = em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                   .setParameter("karteId", karte.getId())
                                                   .setParameter("started", lastDocDate)
                                                   .getResultList();

            DocumentModel latest = latestDocuments.get(0);
            DocumentModel schedule = latest.rpClone();

            logger.info("Created scheduled document from latest document");
            return schedule;

        } catch (Exception e) {
            logger.info("No previous documents found, creating new scheduled document");

            // Create new document
            DocumentModel schedule = new DocumentModel();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            schedule.getDocInfoModel().setDocId(uuid);
            schedule.getDocInfoModel().setDocType(IInfoModel.DOCTYPE_KARTE);
            schedule.getDocInfoModel().setTitle("予定");
            schedule.getDocInfoModel().setPurpose(IInfoModel.PURPOSE_RECORD);
            schedule.getDocInfoModel().setHasRp(false);
            schedule.getDocInfoModel().setVersionNumber("1.0");

            return schedule;
        }
    }

    private void setDocumentInfo(DocumentModel schedule, PatientVisitModel pvt,
                               UserModel user, PatientModel patient,
                               PVTHealthInsuranceModel pvtHealthInsurance, KarteBean karte) {
        Date now = new Date();

        // Set department info
        StringBuilder sb = new StringBuilder();
        sb.append(pvt.getDeptName()).append(",");
        sb.append(pvt.getDeptCode()).append(",");
        sb.append(user.getCommonName()).append(",");

        if (pvt.getDoctorId() != null) {
            sb.append(pvt.getDoctorId()).append(",");
        } else if (user.getOrcaId() != null) {
            sb.append(user.getOrcaId()).append(",");
        } else {
            sb.append(user.getUserId()).append(",");
        }
        sb.append(pvt.getJmariNumber());

        schedule.getDocInfoModel().setDepartmentDesc(sb.toString());
        schedule.getDocInfoModel().setDepartment(pvt.getDeptCode());

        // Set facility and patient info
        schedule.getDocInfoModel().setFacilityName(user.getFacilityModel().getFacilityName());
        schedule.getDocInfoModel().setCreaterLisence(user.getLicenseModel().getLicense());
        schedule.getDocInfoModel().setPatientId(patient.getPatientId());
        schedule.getDocInfoModel().setPatientName(patient.getFullName());
        schedule.getDocInfoModel().setPatientGender(patient.getGenderDesc());

        // Set health insurance
        if (pvtHealthInsurance != null) {
            schedule.getDocInfoModel().setHealthInsurance(pvtHealthInsurance.getInsuranceClassCode());
            schedule.getDocInfoModel().setHealthInsuranceDesc(pvtHealthInsurance.toString());
            schedule.getDocInfoModel().setHealthInsuranceGUID(pvtHealthInsurance.getGUID());
            schedule.getDocInfoModel().setPVTHealthInsuranceModel(pvtHealthInsurance);
        }

        // Set basic attributes
        schedule.setStarted(now);
        schedule.setConfirmed(now);
        schedule.setRecorded(now);
        schedule.setKarteBean(karte);
        schedule.setUserModel(user);
        schedule.setStatus("T");
    }

    private void setupDocumentRelationships(DocumentModel schedule) {
        List<ModuleModel> modules = schedule.getModules();
        if (modules != null) {
            for (ModuleModel module : modules) {
                module.setStarted(schedule.getStarted());
                module.setConfirmed(schedule.getConfirmed());
                module.setRecorded(schedule.getRecorded());
                module.setKarteBean(schedule.getKarteBean());
                module.setUserModel(schedule.getUserModel());
                module.setStatus(schedule.getStatus());
                module.setDocumentModel(schedule);
            }
        }
    }

    /**
     * Remove patient visit and associated documents.
     * @param pvtPK patient visit primary key
     * @param patientPK patient primary key
     * @param startDate appointment date
     * @return number of deleted items
     */
    public int removePvt(long pvtPK, long patientPK, Date startDate) {
        // Remove patient visit
        PatientVisitModel pvt = em.find(PatientVisitModel.class, pvtPK);
        em.remove(pvt);

        // Get karte
        List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                  .setParameter("patientPk", patientPK)
                                  .getResultList();
        KarteBean karte = kartes.get(0);

        // Find documents for this date
        List<DocumentModel> documents = em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                        .setParameter("karteId", karte.getId())
                                        .setParameter("started", startDate)
                                        .getResultList();

        if (documents.isEmpty()) {
            return 1;
        }

        // Delete documents
        int count = 1;
        for (DocumentModel document : documents) {
            List<String> deletedIds = deleteDocument(document.getId());
            count += deletedIds.size();
        }

        return count;
    }

    /**
     * Delete document with cascade operations.
     * @param id document ID
     * @return list of deleted document IDs
     */
    public List<String> deleteDocument(long id) {
        // Check for references
        List refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
                     .setParameter("id", id)
                     .getResultList();

        if (refs != null && !refs.isEmpty()) {
            throw new RuntimeException("他のドキュメントから参照されているため削除できません。");
        }

        Date ended = new Date();
        List<String> deletedIds = new ArrayList<>();

        // Delete document chain
        while (true) {
            try {
                DocumentModel document = em.find(DocumentModel.class, id);
                if (document == null) break;

                // Mark as deleted
                document.setStatus(IInfoModel.STATUS_DELETE);
                document.setEnded(ended);
                deletedIds.add(document.getDocInfoModel().getDocId());

                // Delete related modules
                List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                                            .setParameter("id", id)
                                            .getResultList();
                for (ModuleModel module : modules) {
                    module.setStatus(IInfoModel.STATUS_DELETE);
                    module.setEnded(ended);
                }

                // Delete related schemas
                List<SchemaModel> schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                                            .setParameter("id", id)
                                            .getResultList();
                for (SchemaModel schema : schemas) {
                    schema.setStatus(IInfoModel.STATUS_DELETE);
                    schema.setEnded(ended);
                }

                // Delete related attachments
                List<AttachmentModel> attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                                                    .setParameter("id", id)
                                                    .getResultList();
                for (AttachmentModel attachment : attachments) {
                    attachment.setStatus(IInfoModel.STATUS_DELETE);
                    attachment.setEnded(ended);
                }

                // Move to linked document
                id = document.getLinkId();

            } catch (Exception e) {
                break;
            }
        }

        return deletedIds;
    }

    private Date dateFromString(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
