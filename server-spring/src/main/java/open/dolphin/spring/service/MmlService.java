package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.beans.XMLDecoder;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for Medical Markup Language (MML) processing and data export.
 * Migrated from MmlServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class MmlService {

    private static final Logger logger = Logger.getLogger(MmlService.class.getName());

    // Parameter constants
    private static final String KARTE_ID = "karteId";
    private static final String ID = "id";
    private static final String FID = "fid";
    private static final String PK = "pk";
    private static final String MID = "mid";

    // Query constants
    private static final String QUERY_DIAGNOSIS_BY_KARTE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId";
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:pk";
    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    private static final String QUERY_ITEM_BY_MID = "from NLaboItem l where l.laboModule.id=:mid order by groupCode,parentCode,itemCode";
    private static final String QUERY_ITEM_BY_MID_ORDERBY_SORTKEY = "from NLaboItem l where l.laboModule.id=:mid order by l.sortKey";
    private static final String QUERY_ITEM_BY_ID = "from LetterItem l where l.module.id=:id";
    private static final String QUERY_TEXT_BY_ID = "from LetterText l where l.module.id=:id";
    private static final String QUERY_DATE_BY_ID = "from LetterDate l where l.module.id=:id";

    private static final String WOLF = "WOLF";

    @PersistenceContext
    private EntityManager em;

    /**
     * Export patient diagnosis data to MML format.
     * @param facilityId facility ID
     * @param index processing index
     * @param patientPK patient primary key
     */
    public void dumpPatientDiagnosisToMML(String facilityId, int index, long patientPK) {
        try {
            // Get patient
            PatientModel patient = em.find(PatientModel.class, patientPK);

            StringBuilder logMsg = new StringBuilder();
            logMsg.append("\n----------------------------\n");
            logMsg.append("処理番号 = ").append(index + 1).append("\n");
            logMsg.append("患者ID = ").append(patient.getPatientId()).append("\n");
            logMsg.append("患者氏名 = ").append(patient.getFullName()).append("\n");

            // Load health insurance
            List<HealthInsuranceModel> insurances = em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                                                     .setParameter(PK, patient.getId())
                                                     .getResultList();

            // Decode health insurance
            for (HealthInsuranceModel insurance : insurances) {
                PVTHealthInsuranceModel pvtInsurance = (PVTHealthInsuranceModel) xmlDecode(insurance.getBeanBytes());
                patient.addPvtHealthInsurance(pvtInsurance);
                logMsg.append("健康保険 = ").append(pvtInsurance.getInsuranceClass()).append("\n");
            }

            // Get karte
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                      .setParameter(PK, patient.getId())
                                      .getResultList();
            KarteBean karte = kartes.get(0);

            // Get diagnoses
            List<RegisteredDiagnosisModel> diagnoses = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE)
                                                        .setParameter(KARTE_ID, karte.getId())
                                                        .getResultList();

            for (RegisteredDiagnosisModel diagnosis : diagnoses) {
                logMsg.append("病名 = ").append(diagnosis.getDiagnosis()).append("\n");
            }

            logger.info(logMsg.toString());

            // Create MML content (simplified for migration)
            String mmlContent = createPatientMMLContent(patient, diagnoses, facilityId);

            // Write to file
            writeMMLFile(getPatientMmlFile(patient.getPatientId()), mmlContent);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to export patient diagnosis to MML", e);
        }
    }

    /**
     * Export medical document to MML format.
     * @param index processing index
     * @param documentPK document primary key
     */
    public void dumpDocumentToMML(int index, long documentPK) {
        try {
            // Get document
            DocumentModel document = em.find(DocumentModel.class, documentPK);
            document.toDetuch();

            // Load modules
            List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                                         .setParameter(ID, document.getId())
                                         .getResultList();

            // Decode modules
            for (ModuleModel module : modules) {
                module.setModel((IInfoModel) xmlDecode(module.getBeanBytes()));
            }
            document.setModules(modules);

            // Load schemas
            List<SchemaModel> schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                                         .setParameter(ID, document.getId())
                                         .getResultList();
            document.setSchema(schemas);

            // Create log message
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("\n----------------------------\n");
            logMsg.append("処理番号 = ").append(index + 1).append("\n");
            logMsg.append("患者ID = ").append(document.getKarteBean().getPatientModel().getPatientId()).append("\n");
            logMsg.append("担当医ID = ").append(document.getUserModel().getUserId()).append("\n");
            logMsg.append("Doc ID = ").append(document.getDocInfoModel().getDocId()).append("\n");
            logMsg.append("文書 status = ").append(document.getStatus()).append("\n");

            logger.info(logMsg.toString());

            // Create MML content (simplified for migration)
            String mmlContent = createDocumentMMLContent(document);

            // Write MML file
            writeMMLFile(getKarteMmlFile(document.getKarteBean().getPatientModel().getPatientId(),
                                       document.getDocInfoModel().getDocId()), mmlContent);

            // Write schema files
            if (document.getSchema() != null && !document.getSchema().isEmpty()) {
                for (SchemaModel schema : document.getSchema()) {
                    writeSchemaFile(document.getKarteBean().getPatientModel().getPatientId(),
                                  schema.getExtRefModel().getHref(), schema.getJpegByte());
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to export document to MML", e);
        }
    }

    /**
     * Get facility patient count.
     * @param facilityId facility ID
     * @return patient count
     */
    public Long getFacilityPatientCount(String facilityId) {
        Long count = em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid", Long.class)
                      .setParameter(FID, facilityId)
                      .getSingleResult();

        logger.info(facilityId + " の患者数 = " + count);
        return count;
    }

    /**
     * Get facility document count.
     * @param facilityId facility ID
     * @return document count
     */
    public Long getFacilityDocumentCount(String facilityId) {
        Long count = em.createQuery("select count(*) from DocumentModel d where d.creator.userId like :fid", Long.class)
                      .setParameter(FID, facilityId + ":%")
                      .getSingleResult();

        logger.info(facilityId + " のカルテ件数 = " + count);
        return count;
    }

    /**
     * Get facility document list.
     * @param facilityId facility ID
     * @return list of document IDs
     */
    public List<Long> getFacilityDocumentList(String facilityId) {
        List<DocumentModel> documents = em.createQuery("from DocumentModel d where d.creator.userId like :fid")
                                        .setParameter(FID, facilityId + ":%")
                                        .getResultList();

        logger.info(facilityId + " のカルテ件数 = " + documents.size());

        List<Long> documentIds = new ArrayList<>();
        for (DocumentModel document : documents) {
            documentIds.add(document.getId());
        }

        return documentIds;
    }

    /**
     * Export patient data to JSON format.
     * @param index processing index
     * @param patientPK patient primary key
     */
    public void patientToJSON(int index, long patientPK) {
        try {
            // Get patient with health insurance
            PatientModel patient = getPatientByPK(patientPK);

            StringBuilder logMsg = new StringBuilder();
            logMsg.append("\n----------------------------\n");
            logMsg.append("処理番号 = ").append(index + 1).append("\n");
            logMsg.append("患者ID = ").append(patient.getPatientId()).append("\n");
            logMsg.append("患者氏名 = ").append(patient.getFullName()).append("\n");
            logger.info(logMsg.toString());

            // Convert to JSON (simplified for migration)
            String jsonContent = createPatientJSONContent(patient);

            // Write to file
            writeMMLFile(getPatientMmlFile(patient.getPatientId()), jsonContent);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to export patient to JSON", e);
        }
    }

    /**
     * Get facility patient list.
     * @param facilityId facility ID
     * @return list of patient IDs
     */
    public List<Long> getFacilityPatientList(String facilityId) {
        List<PatientModel> patients = em.createQuery("from PatientModel m where m.facilityId=:fid")
                                       .setParameter(FID, facilityId)
                                       .getResultList();

        logger.info(facilityId + ":患者総数 = " + patients.size());

        List<Long> patientIds = new ArrayList<>();
        for (PatientModel patient : patients) {
            patientIds.add(patient.getId());
        }

        return patientIds;
    }

    /**
     * Get patient by primary key with health insurance.
     * @param patientPK patient primary key
     * @return patient model
     */
    public PatientModel getPatientByPK(long patientPK) {
        PatientModel patient = em.find(PatientModel.class, patientPK);

        List<HealthInsuranceModel> insurances = em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                                                .setParameter(PK, patient.getId())
                                                .getResultList();
        patient.setHealthInsurances(insurances);

        return patient;
    }

    /**
     * Get facility disease list.
     * @param facilityId facility ID
     * @return list of disease IDs
     */
    public List<Long> getFacilityDiseaseList(String facilityId) {
        List<RegisteredDiagnosisModel> diseases = em.createQuery("from RegisteredDiagnosisModel m where m.creator.userId like :fid")
                                                   .setParameter(FID, facilityId + ":%")
                                                   .getResultList();

        logger.info(facilityId + ":病名総数 = " + diseases.size());

        List<Long> diseaseIds = new ArrayList<>();
        for (RegisteredDiagnosisModel disease : diseases) {
            diseaseIds.add(disease.getId());
        }

        return diseaseIds;
    }

    /**
     * Get disease by primary key.
     * @param diseasePK disease primary key
     * @return disease model
     */
    public RegisteredDiagnosisModel getDiseaseByPK(long diseasePK) {
        return em.find(RegisteredDiagnosisModel.class, diseasePK);
    }

    /**
     * Get facility memo list.
     * @param facilityId facility ID
     * @return list of memo IDs
     */
    public List<Long> getFacilityMemoList(String facilityId) {
        List<PatientMemoModel> memos = em.createQuery("from PatientMemoModel m where m.creator.userId like :fid")
                                        .setParameter(FID, facilityId + ":%")
                                        .getResultList();

        logger.info(facilityId + ":メモ総数 = " + memos.size());

        List<Long> memoIds = new ArrayList<>();
        for (PatientMemoModel memo : memos) {
            memoIds.add(memo.getId());
        }

        return memoIds;
    }

    /**
     * Get memo by primary key.
     * @param memoPK memo primary key
     * @return memo model
     */
    public PatientMemoModel getMemoByPK(long memoPK) {
        return em.find(PatientMemoModel.class, memoPK);
    }

    /**
     * Get facility observation list.
     * @param facilityId facility ID
     * @return list of observation IDs
     */
    public List<Long> getFacilityObservationList(String facilityId) {
        List<ObservationModel> observations = em.createQuery("from ObservationModel m where m.creator.userId like :fid")
                                               .setParameter(FID, facilityId + ":%")
                                               .getResultList();

        logger.info(facilityId + ":オブザべーション総数 = " + observations.size());

        List<Long> observationIds = new ArrayList<>();
        for (ObservationModel observation : observations) {
            observationIds.add(observation.getId());
        }

        return observationIds;
    }

    /**
     * Get observation by primary key.
     * @param observationPK observation primary key
     * @return observation model
     */
    public ObservationModel getObservationByPK(long observationPK) {
        return em.find(ObservationModel.class, observationPK);
    }

    /**
     * Get facility karte list.
     * @param facilityId facility ID
     * @return list of karte IDs
     */
    public List<Long> getFacilityKarteList(String facilityId) {
        List<DocumentModel> documents = em.createQuery("from DocumentModel m where m.creator.userId like :fid")
                                        .setParameter(FID, facilityId + ":%")
                                        .getResultList();

        logger.info(facilityId + ":カルテ総数 = " + documents.size());

        List<Long> documentIds = new ArrayList<>();
        for (DocumentModel document : documents) {
            documentIds.add(document.getId());
        }

        return documentIds;
    }

    /**
     * Get karte by primary key with all components.
     * @param kartePK karte primary key
     * @return document model
     */
    public DocumentModel getKarteByPK(long kartePK) {
        DocumentModel document = em.find(DocumentModel.class, kartePK);

        // Load modules
        List<ModuleModel> modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                                    .setParameter(ID, document.getId())
                                    .getResultList();
        document.setModules(modules);

        // Load schemas
        List<SchemaModel> schemas = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                                    .setParameter(ID, document.getId())
                                    .getResultList();
        document.setSchema(schemas);

        // Load attachments
        List<AttachmentModel> attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                                            .setParameter(ID, document.getId())
                                            .getResultList();
        document.setAttachment(attachments);

        return document;
    }

    /**
     * Get facility letter list.
     * @param facilityId facility ID
     * @return list of letter IDs
     */
    public List<Long> getFacilityLetterList(String facilityId) {
        List<LetterModule> letters = em.createQuery("from LetterModule m where m.creator.userId like :fid")
                                      .setParameter(FID, facilityId + ":%")
                                      .getResultList();

        logger.info(facilityId + ":紹介状総数 = " + letters.size());

        List<Long> letterIds = new ArrayList<>();
        for (LetterModule letter : letters) {
            letterIds.add(letter.getId());
        }

        return letterIds;
    }

    /**
     * Get letter by primary key with all components.
     * @param letterPK letter primary key
     * @return letter module
     */
    public LetterModule getLetterByPK(long letterPK) {
        LetterModule letter = em.find(LetterModule.class, letterPK);

        // Load items
        List<LetterItem> items = em.createQuery(QUERY_ITEM_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterItems(items);

        // Load texts
        List<LetterText> texts = em.createQuery(QUERY_TEXT_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterTexts(texts);

        // Load dates
        List<LetterDate> dates = em.createQuery(QUERY_DATE_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterDates(dates);

        return letter;
    }

    /**
     * Get facility lab test list.
     * @param facilityId facility ID
     * @return list of lab test IDs
     */
    public List<Long> getFacilityLabtestList(String facilityId) {
        List<NLaboModule> labTests = em.createQuery("from NLaboModule m where m.creator.userId like :fid")
                                     .setParameter(FID, facilityId + ":%")
                                     .getResultList();

        logger.info(facilityId + ":検査総数 = " + labTests.size());

        List<Long> labTestIds = new ArrayList<>();
        for (NLaboModule labTest : labTests) {
            labTestIds.add(labTest.getId());
        }

        return labTestIds;
    }

    /**
     * Get lab test by primary key with items.
     * @param labTestPK lab test primary key
     * @return lab test module
     */
    public NLaboModule getLabtestByPK(long labTestPK) {
        NLaboModule labTest = em.find(NLaboModule.class, labTestPK);

        List<NLaboItem> items;
        if (WOLF.equals(labTest.getReportFormat())) {
            items = em.createQuery(QUERY_ITEM_BY_MID_ORDERBY_SORTKEY)
                     .setParameter(MID, labTest.getId())
                     .getResultList();
        } else {
            items = em.createQuery(QUERY_ITEM_BY_MID)
                     .setParameter(MID, labTest.getId())
                     .getResultList();
        }
        labTest.setItems(items);

        return labTest;
    }

    // Helper methods for file operations and content creation

    private String createPatientMMLContent(PatientModel patient, List<RegisteredDiagnosisModel> diagnoses, String facilityId) {
        // Simplified MML content creation for migration
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<mml:patient xmlns:mml=\"http://www.medxml.net/MML/\">\n");
        content.append("  <mml:id>").append(patient.getPatientId()).append("</mml:id>\n");
        content.append("  <mml:name>").append(patient.getFullName()).append("</mml:name>\n");
        content.append("  <mml:facility>").append(facilityId).append("</mml:facility>\n");
        content.append("</mml:patient>\n");
        return content.toString();
    }

    private String createDocumentMMLContent(DocumentModel document) {
        // Simplified MML content creation for migration
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<mml:document xmlns:mml=\"http://www.medxml.net/MML/\">\n");
        content.append("  <mml:docId>").append(document.getDocInfoModel().getDocId()).append("</mml:docId>\n");
        content.append("  <mml:status>").append(document.getStatus()).append("</mml:status>\n");
        content.append("</mml:document>\n");
        return content.toString();
    }

    private String createPatientJSONContent(PatientModel patient) {
        // Simplified JSON content creation for migration
        StringBuilder content = new StringBuilder();
        content.append("{\n");
        content.append("  \"patientId\": \"").append(patient.getPatientId()).append("\",\n");
        content.append("  \"fullName\": \"").append(patient.getFullName()).append("\"\n");
        content.append("}\n");
        return content.toString();
    }

    private void writeMMLFile(File file, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
             FileChannel channel = fos.getChannel()) {

            byte[] data = content.getBytes("UTF-8");
            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.clear();
            buffer.put(data);
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }

    private void writeSchemaFile(String patientId, String href, byte[] data) throws IOException {
        File file = getSchemaFile(patientId, href);

        try (FileOutputStream fos = new FileOutputStream(file);
             FileChannel channel = fos.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.clear();
            buffer.put(data);
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }

    private File getPatientMmlFile(String patientId) {
        String baseDir = System.getProperty("user.home") + "/mml/patient/";
        File patientDir = new File(baseDir);
        patientDir.mkdirs();
        return new File(patientDir, patientId + ".xml");
    }

    private File getKarteMmlFile(String patientId, String docId) {
        String baseDir = System.getProperty("user.home") + "/mml/karte/" + patientId + "/";
        File patientDir = new File(baseDir);
        patientDir.mkdirs();
        return new File(patientDir, docId + ".xml");
    }

    private File getSchemaFile(String patientId, String href) {
        String baseDir = System.getProperty("user.home") + "/mml/karte/" + patientId + "/";
        File patientDir = new File(baseDir);
        patientDir.mkdirs();
        return new File(patientDir, href);
    }

    private Object xmlDecode(byte[] bytes) {
        XMLDecoder decoder = new XMLDecoder(
            new BufferedInputStream(
                new ByteArrayInputStream(bytes)));
        return decoder.readObject();
    }
}
