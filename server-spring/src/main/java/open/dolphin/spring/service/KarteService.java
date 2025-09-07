package open.dolphin.spring.service;

import open.dolphin.spring.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * Spring Boot service for medical record (Karte) operations.
 * Migrated from KarteServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class KarteService {

    private static final Logger logger = Logger.getLogger(KarteService.class.getName());

    // Parameters
    private static final String PATIENT_PK = "patientPk";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String ID = "id";
    private static final String ENTITY = "entity";
    private static final String FID = "fid";
    private static final String PID = "pid";

    // Query constants
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    private static final String QUERY_ALLERGY = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    private static final String QUERY_BODY_HEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyHeight'";
    private static final String QUERY_BODY_WEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyWeight'";
    private static final String QUERY_PATIENT_VISIT = "from PatientVisitModel p where p.patient.id=:patientPk and p.pvtDate >= :fromDate and p.status!=64";
    private static final String QUERY_DOC_INFO = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";
    private static final String QUERY_DOCUMENT_INCLUDE_MODIFIED = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and d.status !='D'";
    private static final String QUERY_DOCUMENT = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";
    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id order by m.id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id order by i.id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id order by a.id";
    private static final String QUERY_ATTACHMENT_BY_ID = "from AttachmentModel a where a.id=:id";
    private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.started between :fromDate and :toDate and m.status='F' order by m.started";
    private static final String QUERY_SCHEMA_BY_KARTE_ID = "from SchemaModel i where i.karte.id =:karteId and i.started between :fromDate and :toDate and i.status='F'";
    private static final String QUERY_SCHEMA_BY_FACILITY_ID = "from SchemaModel i where i.karte.patient.facilityId like :fid and i.extRef.sop is not null and i.status='F'";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_DATE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.started >= :fromDate";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_DATE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.started >= :fromDate and r.ended is NULL";
    private static final String QUERY_DIAGNOSIS_BY_KARTE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL";
    private static final String QUERY_LETTER_BY_KARTE_ID = "from TouTouLetter f where f.karte.id=:karteId";
    private static final String QUERY_REPLY_BY_KARTE_ID = "from TouTouReply f where f.karte.id=:karteId";
    private static final String QUERY_LETTER_BY_ID = "from TouTouLetter t where t.id=:id";
    private static final String QUERY_REPLY_BY_ID = "from TouTouReply t where t.id=:id";
    private static final String QUERY_APPO_BY_KARTE_ID_PERIOD = "from AppointmentModel a where a.karte.id = :karteId and a.date between :fromDate and :toDate";
    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    private static final String QUERY_LASTDOC_DATE = "select max(m.started) from DocumentModel m where m.karte.id = :karteId and (m.status = 'F' or m.status = 'T')";
    private static final String QUERY_FREEDOCU_BY_FPID = "from PatientFreeDocumentModel p where p.facilityPatId=:fpid";
    private static final String FPID = "fpid";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ClaimSenderService claimSenderService;

    /**
     * Get patient medical record with comprehensive information by patient PK.
     */
    public KarteBean getKarte(long patientPK, Date fromDate) {
        try {
            // Get karte
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                    .setParameter(PATIENT_PK, patientPK)
                    .getResultList();

            if (kartes.isEmpty()) {
                return null;
            }

            KarteBean karte = kartes.get(0);
            long karteId = karte.getId();

            // Load allergies
            loadAllergies(karte, karteId);

            // Load physical measurements
            loadPhysicalMeasurements(karte, karteId);

            // Load patient visits
            loadPatientVisits(karte, patientPK, fromDate);

            // Load document history
            loadDocumentHistory(karte, karteId, fromDate);

            // Load patient memos
            loadPatientMemos(karte, karteId);

            // Load last document date
            loadLastDocumentDate(karte, karteId);

            return karte;

        } catch (Exception e) {
            logger.warning("Error getting karte by PK: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get patient medical record with comprehensive information by facility and patient ID.
     */
    public KarteBean getKarte(String fid, String pid, Date fromDate) {
        try {
            // Get patient
            PatientModel patient = (PatientModel) em.createQuery(QUERY_PATIENT_BY_FID_PID)
                    .setParameter(FID, fid)
                    .setParameter(PID, pid)
                    .getSingleResult();

            long patientPK = patient.getId();

            // Get karte
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                    .setParameter(PATIENT_PK, patientPK)
                    .getResultList();

            if (kartes.isEmpty()) {
                return null;
            }

            KarteBean karte = kartes.get(0);
            long karteId = karte.getId();

            // Load allergies
            loadAllergies(karte, karteId);

            // Load physical measurements
            loadPhysicalMeasurements(karte, karteId);

            // Load patient visits
            loadPatientVisits(karte, patientPK, fromDate);

            // Load document history
            loadDocumentHistory(karte, karteId, fromDate);

            // Load patient memos
            loadPatientMemos(karte, karteId);

            // Load last document date
            loadLastDocumentDate(karte, karteId);

            return karte;

        } catch (Exception e) {
            logger.warning("Error getting karte: " + e.getMessage());
            return null;
        }
    }

    private void loadAllergies(KarteBean karte, long karteId) {
        List<ObservationModel> allergyObservations =
                em.createQuery(QUERY_ALLERGY)
                  .setParameter(KARTE_ID, karteId)
                  .getResultList();

        if (!allergyObservations.isEmpty()) {
            List<AllergyModel> allergies = new ArrayList<>(allergyObservations.size());
            for (ObservationModel observation : allergyObservations) {
                AllergyModel allergy = new AllergyModel();
                allergy.setObservationId(observation.getId());
                allergy.setFactor(observation.getPhenomenon());
                allergy.setSeverity(observation.getCategoryValue());
                allergy.setIdentifiedDate(observation.confirmDateAsString());
                allergy.setMemo(observation.getMemo());
                allergies.add(allergy);
            }
            karte.setAllergies(allergies);
        }
    }

    private void loadPhysicalMeasurements(KarteBean karte, long karteId) {
        // Load height data
        List<ObservationModel> heightObservations =
                em.createQuery(QUERY_BODY_HEIGHT)
                  .setParameter(KARTE_ID, karteId)
                  .getResultList();

        if (!heightObservations.isEmpty()) {
            List<PhysicalModel> heights = new ArrayList<>(heightObservations.size());
            for (ObservationModel observation : heightObservations) {
                PhysicalModel physical = new PhysicalModel();
                physical.setHeightId(observation.getId());
                physical.setHeight(observation.getValue());
                physical.setIdentifiedDate(observation.confirmDateAsString());
                physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                heights.add(physical);
            }
            karte.setHeights(heights);
        }

        // Load weight data
        List<ObservationModel> weightObservations =
                em.createQuery(QUERY_BODY_WEIGHT)
                  .setParameter(KARTE_ID, karteId)
                  .getResultList();

        if (!weightObservations.isEmpty()) {
            List<PhysicalModel> weights = new ArrayList<>(weightObservations.size());
            for (ObservationModel observation : weightObservations) {
                PhysicalModel physical = new PhysicalModel();
                physical.setWeightId(observation.getId());
                physical.setWeight(observation.getValue());
                physical.setIdentifiedDate(observation.confirmDateAsString());
                physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                weights.add(physical);
            }
            karte.setWeights(weights);
        }
    }

    private void loadPatientVisits(KarteBean karte, long patientPK, Date fromDate) {
        List<PatientVisitModel> visits =
                em.createQuery(QUERY_PATIENT_VISIT)
                  .setParameter(PATIENT_PK, patientPK)
                  .setParameter(FROM_DATE, ModelUtils.getDateAsString(fromDate))
                  .getResultList();

        if (!visits.isEmpty()) {
            List<String> visitDates = new ArrayList<>(visits.size());
            for (PatientVisitModel visit : visits) {
                visitDates.add(visit.getPvtDate());
            }
            karte.setPatientVisits(visitDates);
        }
    }

    private void loadDocumentHistory(KarteBean karte, long karteId, Date fromDate) {
        List<DocumentModel> documents =
                em.createQuery(QUERY_DOC_INFO)
                  .setParameter(KARTE_ID, karteId)
                  .setParameter(FROM_DATE, fromDate)
                  .getResultList();

        if (!documents.isEmpty()) {
            List<DocInfoModel> docInfos = new ArrayList<>(documents.size());
            for (DocumentModel doc : documents) {
                doc.toDetuch();
                docInfos.add(doc.getDocInfoModel());
            }
            karte.setDocInfoList(docInfos);
        }
    }

    private void loadPatientMemos(KarteBean karte, long karteId) {
        List<PatientMemoModel> memos =
                em.createQuery(QUERY_PATIENT_MEMO)
                  .setParameter(KARTE_ID, karteId)
                  .getResultList();

        if (!memos.isEmpty()) {
            karte.setMemoList(memos);
        }
    }

    private void loadLastDocumentDate(KarteBean karte, long karteId) {
        try {
            Date lastDocDate = (Date)
                    em.createQuery(QUERY_LASTDOC_DATE)
                      .setParameter(KARTE_ID, karteId)
                      .getSingleResult();
            karte.setLastDocDate(lastDocDate);
        } catch (NoResultException e) {
            // No documents found
        }
    }

    /**
     * Get document list with optional modified documents.
     */
    public List<DocInfoModel> getDocumentList(long karteId, Date fromDate, boolean includeModified) {
        List<DocumentModel> documents;

        if (includeModified) {
            documents = em.createQuery(QUERY_DOCUMENT_INCLUDE_MODIFIED)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate)
                    .getResultList();
        } else {
            documents = em.createQuery(QUERY_DOCUMENT)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate)
                    .getResultList();
        }

        List<DocInfoModel> result = new ArrayList<>();
        for (DocumentModel doc : documents) {
            doc.toDetuch();
            result.add(doc.getDocInfoModel());
        }
        return result;
    }

    /**
     * Get full documents with modules, schemas, and attachments.
     */
    public List<DocumentModel> getDocuments(List<Long> ids) {
        List<DocumentModel> ret = new ArrayList<>();

        for (Long id : ids) {
            DocumentModel document = em.find(DocumentModel.class, id);

            // Load modules
            List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            document.setModules(modules);

            // Load schemas
            List<SchemaModel> schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            document.setSchema(schemas);

            // Load attachments
            List<AttachmentModel> attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            document.setAttachment(attachments);

            ret.add(document);
        }

        // Detach documents for client use
        for (DocumentModel doc : ret) {
            doc.toDetuch();
        }

        return ret;
    }

    /**
     * Add new document with CLAIM sending support.
     */
    public long addDocument(DocumentModel document) {
        em.persist(document);
        long id = document.getId();

        // Handle document versioning
        long parentPk = document.getDocInfoModel().getParentPk();
        if (parentPk != 0L) {
            updateParentDocument(parentPk, document.getConfirmed());
        }

        // Send CLAIM if required
        if (!document.getDocInfoModel().isSendClaim()) {
            return id;
        }

        claimSenderService.sendDocument(document);
        return id;
    }

    private void updateParentDocument(long parentPk, Date ended) {
        DocumentModel old = em.find(DocumentModel.class, parentPk);
        if (old != null) {
            old.setEnded(ended);
            old.setStatus(IInfoModel.STATUS_MODIFIED);

            // Update related modules
            updateRelatedEntities(parentPk, ended, QUERY_MODULE_BY_DOC_ID, ModuleModel.class);

            // Update related schemas
            updateRelatedEntities(parentPk, ended, QUERY_SCHEMA_BY_DOC_ID, SchemaModel.class);

            // Update related attachments
            updateRelatedEntities(parentPk, ended, QUERY_ATTACHMENT_BY_DOC_ID, AttachmentModel.class);
        }
    }

    private void updateRelatedEntities(long documentId, Date ended, String query, Class<?> entityClass) {
        List entities = em.createQuery(query)
                .setParameter(ID, documentId)
                .getResultList();

        for (Object entity : entities) {
            if (entity instanceof ModuleModel) {
                ((ModuleModel) entity).setEnded(ended);
                ((ModuleModel) entity).setStatus(IInfoModel.STATUS_MODIFIED);
            } else if (entity instanceof SchemaModel) {
                ((SchemaModel) entity).setEnded(ended);
                ((SchemaModel) entity).setStatus(IInfoModel.STATUS_MODIFIED);
            } else if (entity instanceof AttachmentModel) {
                ((AttachmentModel) entity).setEnded(ended);
                ((AttachmentModel) entity).setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }
    }

    /**
     * Delete document logically.
     */
    public List<String> deleteDocument(long id) {
        // Check for references
        List refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
                .setParameter(ID, id)
                .getResultList();

        if (refs != null && !refs.isEmpty()) {
            throw new RuntimeException("他のドキュメントから参照されているため削除できません。");
        }

        Date ended = new Date();
        List<String> deletedDocIds = new ArrayList<>();

        // Delete document chain
        while (true) {
            try {
                DocumentModel delete = em.find(DocumentModel.class, id);
                if (delete == null) break;

                delete.setStatus(IInfoModel.STATUS_DELETE);
                delete.setEnded(ended);
                deletedDocIds.add(delete.getDocInfoModel().getDocId());

                // Delete related entities
                deleteRelatedEntities(id, ended);

                // Move to linked document
                id = delete.getLinkId();

            } catch (Exception e) {
                break;
            }
        }

        return deletedDocIds;
    }

    private void deleteRelatedEntities(long documentId, Date ended) {
        // Delete modules
        List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                .setParameter(ID, documentId)
                .getResultList();
        for (ModuleModel module : modules) {
            module.setStatus(IInfoModel.STATUS_DELETE);
            module.setEnded(ended);
        }

        // Delete schemas
        List<SchemaModel> schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                .setParameter(ID, documentId)
                .getResultList();
        for (SchemaModel schema : schemas) {
            schema.setStatus(IInfoModel.STATUS_DELETE);
            schema.setEnded(ended);
        }

        // Delete attachments
        List<AttachmentModel> attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                .setParameter(ID, documentId)
                .getResultList();
        for (AttachmentModel attachment : attachments) {
            attachment.setStatus(IInfoModel.STATUS_DELETE);
            attachment.setEnded(ended);
        }
    }

    /**
     * Get modules by entity and date range.
     */
    public List<List<ModuleModel>> getModules(long karteId, String entity, List fromDate, List toDate) {
        int len = fromDate.size();
        List<List<ModuleModel>> ret = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_ENTITY)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(ENTITY, entity)
                    .setParameter(FROM_DATE, fromDate.get(i))
                    .setParameter(TO_DATE, toDate.get(i))
                    .getResultList();

            ret.add(modules);
        }

        return ret;
    }

    /**
     * Get diagnosis information.
     */
    public List<RegisteredDiagnosisModel> getDiagnosis(long karteId, Date fromDate, boolean activeOnly) {
        String query;
        if (fromDate != null) {
            query = activeOnly ? QUERY_DIAGNOSIS_BY_KARTE_DATE_ACTIVEONLY : QUERY_DIAGNOSIS_BY_KARTE_DATE;
            return em.createQuery(query)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate)
                    .getResultList();
        } else {
            query = activeOnly ? QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY : QUERY_DIAGNOSIS_BY_KARTE;
            return em.createQuery(query)
                    .setParameter(KARTE_ID, karteId)
                    .getResultList();
        }
    }

    /**
     * Add observations (allergies, physical measurements, etc.).
     */
    public List<Long> addObservations(List<ObservationModel> observations) {
        if (observations != null && !observations.isEmpty()) {
            List<Long> ret = new ArrayList<>(observations.size());
            for (ObservationModel model : observations) {
                em.persist(model);
                ret.add(model.getId());
            }
            return ret;
        }
        return null;
    }

    /**
     * Get observations by criteria.
     */
    public List<ObservationModel> getObservations(long karteId, String observation, String phenomenon, Date firstConfirmed) {
        String query;
        Map<String, Object> params = new HashMap<>();
        params.put(KARTE_ID, karteId);

        if (observation != null) {
            if (firstConfirmed != null) {
                query = "from ObservationModel o where o.karte.id=:karteId and o.observation=:observation and o.started >= :firstConfirmed";
                params.put("observation", observation);
                params.put("firstConfirmed", firstConfirmed);
            } else {
                query = "from ObservationModel o where o.karte.id=:karteId and o.observation=:observation";
                params.put("observation", observation);
            }
        } else if (phenomenon != null) {
            if (firstConfirmed != null) {
                query = "from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon and o.started >= :firstConfirmed";
                params.put("phenomenon", phenomenon);
                params.put("firstConfirmed", firstConfirmed);
            } else {
                query = "from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon";
                params.put("phenomenon", phenomenon);
            }
        } else {
            return new ArrayList<>();
        }

        jakarta.persistence.Query q = em.createQuery(query);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }

        return q.getResultList();
    }

    /**
     * Get attachment by ID.
     */
    public AttachmentModel getAttachment(long pk) {
        try {
            return (AttachmentModel) em.createQuery(QUERY_ATTACHMENT_BY_ID)
                    .setParameter(ID, pk)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get all documents for a patient (for PDF export).
     */
    public List<DocumentModel> getAllDocument(long patientPK) {
        List<DocumentModel> result = new ArrayList<>();

        try {
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                    .setParameter(PATIENT_PK, patientPK)
                    .getResultList();

            if (kartes.isEmpty()) {
                return result;
            }

            KarteBean karte = kartes.get(0);
            List<DocumentModel> documents = em.createQuery(
                    "from DocumentModel d where d.karte.id=:karteId and (d.status='F' or d.status='T')")
                    .setParameter(KARTE_ID, karte.getId())
                    .getResultList();

            for (DocumentModel model : documents) {
                model.toDetuch();
                loadDocumentComponents(model);
                result.add(model);
            }

        } catch (NoResultException e) {
            // No karte found for patient
        }

        return result;
    }

    private void loadDocumentComponents(DocumentModel model) {
        long id = model.getId();

        try {
            List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            model.setModules(modules);
        } catch (NoResultException e) {
            // No modules found
        }

        try {
            List schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            model.setSchema(schemas);
        } catch (NoResultException e) {
            // No schemas found
        }

        try {
            List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                    .setParameter(ID, id)
                    .getResultList();
            model.setAttachment(attachments);
        } catch (NoResultException e) {
            // No attachments found
        }
    }
}
