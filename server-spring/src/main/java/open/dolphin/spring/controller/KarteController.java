package open.dolphin.spring.controller;

import open.dolphin.infomodel.*;
import open.dolphin.spring.service.KarteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for medical record (Karte) operations.
 * Migrated from KarteResource (JAX-RS).
 */
@RestController
@RequestMapping("/karte")
public class KarteController {

    @Autowired
    private KarteService karteService;

    /**
     * Get karte by patient ID with facility ID.
     * Format: pid,fromDate
     */
    @GetMapping("/pid/{param}")
    public ResponseEntity<KarteBean> getKarteByPid(
            @RequestParam("fid") String fid,
            @PathVariable("param") String param) {

        String[] params = param.split(",");
        String pid = params[0];
        Date fromDate = parseDate(params[1]);

        KarteBean karte = karteService.getKarte(fid, pid, fromDate);
        return ResponseEntity.ok(karte);
    }

    /**
     * Get karte by patient primary key.
     * Format: patientPK,fromDate
     */
    @GetMapping("/{param}")
    public ResponseEntity<KarteBean> getKarte(@PathVariable("param") String param) {
        String[] params = param.split(",");
        long patientPK = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);

        KarteBean karte = karteService.getKarte(patientPK, fromDate);
        return ResponseEntity.ok(karte);
    }

    /**
     * Get document list.
     * Format: karteId,fromDate,includeModified
     */
    @GetMapping("/docinfo/{param}")
    public ResponseEntity<List<DocInfoModel>> getDocumentList(@PathVariable("param") String param) {
        String[] params = param.split(",");
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);
        boolean includeModified = Boolean.parseBoolean(params[2]);

        List<DocInfoModel> documents = karteService.getDocumentList(karteId, fromDate, includeModified);
        return ResponseEntity.ok(documents);
    }

    /**
     * Get documents by IDs.
     * Format: id1,id2,id3,...
     */
    @GetMapping("/documents/{param}")
    public ResponseEntity<List<DocumentModel>> getDocuments(@PathVariable("param") String param) {
        String[] params = param.split(",");
        List<Long> ids = new ArrayList<>();
        for (String id : params) {
            ids.add(Long.parseLong(id));
        }

        List<DocumentModel> documents = karteService.getDocuments(ids);

        // Clear attachment bytes for performance (same as original)
        for (DocumentModel doc : documents) {
            List<AttachmentModel> attachments = doc.getAttachment();
            if (attachments != null) {
                for (AttachmentModel attachment : attachments) {
                    attachment.setBytes(null);
                }
            }
        }

        return ResponseEntity.ok(documents);
    }

    /**
     * Add new document.
     */
    @PostMapping("/document")
    public ResponseEntity<Long> addDocument(@RequestBody DocumentModel document) {
        // Build relationships
        buildDocumentRelationships(document);

        long result = karteService.addDocument(document);
        return ResponseEntity.ok(result);
    }

    /**
     * Add document and update PVT state.
     * Format: pvtPK,state
     */
    @PostMapping("/document/pvt/{params}")
    public ResponseEntity<Long> addDocumentWithPvtUpdate(
            @PathVariable("params") String param,
            @RequestBody DocumentModel document) {

        String[] params = param.split(",");
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);

        // Build relationships
        buildDocumentRelationships(document);

        long result = karteService.addDocument(document);

        // TODO: Update PVT state if needed
        // This would require access to PVTService

        return ResponseEntity.ok(result);
    }

    /**
     * Update document title.
     */
    @PutMapping("/document/{id}")
    public ResponseEntity<Integer> updateDocumentTitle(
            @PathVariable("id") String idStr,
            @RequestBody String title) {

        long id = Long.parseLong(idStr);
        // TODO: Implement title update in KarteService
        return ResponseEntity.ok(1);
    }

    /**
     * Delete document.
     */
    @DeleteMapping("/document/{id}")
    public ResponseEntity<List<String>> deleteDocument(@PathVariable("id") String idStr) {
        long id = Long.parseLong(idStr);
        List<String> deletedIds = karteService.deleteDocument(id);
        return ResponseEntity.ok(deletedIds);
    }

    /**
     * Get modules by entity and date range.
     * Format: karteId,entity,fromDate1,toDate1,fromDate2,toDate2,...
     */
    @GetMapping("/modules/{param}")
    public ResponseEntity<List<List<ModuleModel>>> getModules(@PathVariable("param") String param) {
        String[] params = param.split(",");
        long karteId = Long.parseLong(params[0]);
        String entity = params[1];

        List<Date> fromList = new ArrayList<>();
        List<Date> toList = new ArrayList<>();

        for (int i = 2; i < params.length; i += 2) {
            fromList.add(parseDate(params[i]));
            toList.add(parseDate(params[i + 1]));
        }

        List<List<ModuleModel>> modules = karteService.getModules(karteId, entity, fromList, toList);
        return ResponseEntity.ok(modules);
    }

    /**
     * Get diagnosis information.
     * Format: karteId,fromDate[,activeOnly]
     */
    @GetMapping("/diagnosis/{param}")
    public ResponseEntity<List<RegisteredDiagnosisModel>> getDiagnosis(@PathVariable("param") String param) {
        String[] params = param.split(",");
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);
        boolean activeOnly = params.length >= 3 ? Boolean.parseBoolean(params[2]) : false;

        List<RegisteredDiagnosisModel> diagnoses = karteService.getDiagnosis(karteId, fromDate, activeOnly);
        return ResponseEntity.ok(diagnoses);
    }

    /**
     * Add observations.
     */
    @PostMapping("/observations")
    public ResponseEntity<List<Long>> addObservations(@RequestBody ObservationList observationList) {
        List<Long> result = karteService.addObservations(observationList.getList());
        return ResponseEntity.ok(result);
    }

    /**
     * Get observations.
     * Format: karteId,observation,phenomenon[,firstConfirmed]
     */
    @GetMapping("/observations/{param}")
    public ResponseEntity<List<ObservationModel>> getObservations(@PathVariable("param") String param) {
        String[] params = param.split(",");
        long karteId = Long.parseLong(params[0]);
        String observation = params[1];
        String phenomenon = params[2];
        Date firstConfirmed = params.length >= 4 ? parseDate(params[3]) : null;

        List<ObservationModel> observations = karteService.getObservations(karteId, observation, phenomenon, firstConfirmed);
        return ResponseEntity.ok(observations);
    }

    /**
     * Update patient memo.
     */
    @PutMapping("/memo")
    public ResponseEntity<Integer> updatePatientMemo(@RequestBody PatientMemoModel memo) {
        // TODO: Implement in KarteService
        return ResponseEntity.ok(1);
    }

    /**
     * Get all documents for a patient (PDF export).
     */
    @GetMapping("/docinfo/all/{param}")
    public ResponseEntity<List<DocumentModel>> getAllDocuments(@PathVariable("param") String param) {
        long patientPK = Long.parseLong(param);
        List<DocumentModel> documents = karteService.getAllDocument(patientPK);

        // Clear attachment bytes for performance
        for (DocumentModel doc : documents) {
            List<AttachmentModel> attachments = doc.getAttachment();
            if (attachments != null) {
                for (AttachmentModel attachment : attachments) {
                    attachment.setBytes(null);
                }
            }
        }

        return ResponseEntity.ok(documents);
    }

    /**
     * Get attachment by ID.
     */
    @GetMapping("/attachment/{param}")
    public ResponseEntity<AttachmentModel> getAttachment(@PathVariable("param") String param) {
        long id = Long.parseLong(param);
        AttachmentModel attachment = karteService.getAttachment(id);
        return ResponseEntity.ok(attachment);
    }

    /**
     * Search modules by entities.
     * Format: karteId,fromDate,toDate,entity1,entity2,...
     */
    @GetMapping("/moduleSearch/{param}")
    public ResponseEntity<List<ModuleModel>> getModulesEntitySearch(
            @RequestParam("fid") String fid,
            @PathVariable("param") String param) {

        String[] params = param.split(",");
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1] + " 00:00:00");
        Date toDate = parseDate(params[2] + " 00:00:00");

        List<String> entities = new ArrayList<>();
        for (int i = 3; i < params.length; i++) {
            entities.add(params[i]);
        }

        // TODO: Implement in KarteService
        List<ModuleModel> modules = new ArrayList<>();
        return ResponseEntity.ok(modules);
    }

    // Helper methods
    private void buildDocumentRelationships(DocumentModel document) {
        // Build module relationships
        List<ModuleModel> modules = document.getModules();
        if (modules != null && !modules.isEmpty()) {
            for (ModuleModel module : modules) {
                module.setDocumentModel(document);
            }
        }

        // Build schema relationships
        List<SchemaModel> schemas = document.getSchema();
        if (schemas != null && !schemas.isEmpty()) {
            for (SchemaModel schema : schemas) {
                schema.setDocumentModel(document);
            }
        }

        // Build attachment relationships
        List<AttachmentModel> attachments = document.getAttachment();
        if (attachments != null && !attachments.isEmpty()) {
            for (AttachmentModel attachment : attachments) {
                attachment.setDocumentModel(document);
            }
        }
    }

    private Date parseDate(String dateStr) {
        try {
            // Simple date parsing - could be enhanced
            return new Date(); // Placeholder
        } catch (Exception e) {
            return new Date();
        }
    }
}
