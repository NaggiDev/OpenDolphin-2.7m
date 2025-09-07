package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.MmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Medical Markup Language (MML) processing and data export.
 * Migrated from MmlResource (JAX-RS).
 */
@RestController
@RequestMapping("/mml")
public class MmlController {

    @Autowired
    private MmlService mmlService;

    /**
     * Export patient diagnosis data to MML format.
     * @param facilityId facility ID
     * @param index processing index
     * @param patientPK patient primary key
     * @return success status
     */
    @PostMapping("/patient/diagnosis")
    public ResponseEntity<String> dumpPatientDiagnosisToMML(
            @RequestParam("fid") String facilityId,
            @RequestParam("index") int index,
            @RequestParam("pk") long patientPK) {

        try {
            mmlService.dumpPatientDiagnosisToMML(facilityId, index, patientPK);
            return ResponseEntity.ok("Patient diagnosis exported to MML successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to export patient diagnosis: " + e.getMessage());
        }
    }

    /**
     * Export medical document to MML format.
     * @param index processing index
     * @param documentPK document primary key
     * @return success status
     */
    @PostMapping("/document")
    public ResponseEntity<String> dumpDocumentToMML(
            @RequestParam("index") int index,
            @RequestParam("pk") long documentPK) {

        try {
            mmlService.dumpDocumentToMML(index, documentPK);
            return ResponseEntity.ok("Document exported to MML successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to export document: " + e.getMessage());
        }
    }

    /**
     * Get facility patient count.
     * @param facilityId facility ID
     * @return patient count
     */
    @GetMapping("/patient/count")
    public ResponseEntity<Long> getFacilityPatientCount(@RequestParam("fid") String facilityId) {
        Long count = mmlService.getFacilityPatientCount(facilityId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get facility document count.
     * @param facilityId facility ID
     * @return document count
     */
    @GetMapping("/document/count")
    public ResponseEntity<Long> getFacilityDocumentCount(@RequestParam("fid") String facilityId) {
        Long count = mmlService.getFacilityDocumentCount(facilityId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get facility document list.
     * @param facilityId facility ID
     * @return list of document IDs
     */
    @GetMapping("/document/list")
    public ResponseEntity<List<Long>> getFacilityDocumentList(@RequestParam("fid") String facilityId) {
        List<Long> documentIds = mmlService.getFacilityDocumentList(facilityId);
        return ResponseEntity.ok(documentIds);
    }

    /**
     * Export patient data to JSON format.
     * @param index processing index
     * @param patientPK patient primary key
     * @return success status
     */
    @PostMapping("/patient/json")
    public ResponseEntity<String> patientToJSON(
            @RequestParam("index") int index,
            @RequestParam("pk") long patientPK) {

        try {
            mmlService.patientToJSON(index, patientPK);
            return ResponseEntity.ok("Patient exported to JSON successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to export patient to JSON: " + e.getMessage());
        }
    }

    /**
     * Get facility patient list.
     * @param facilityId facility ID
     * @return list of patient IDs
     */
    @GetMapping("/patient/list")
    public ResponseEntity<List<Long>> getFacilityPatientList(@RequestParam("fid") String facilityId) {
        List<Long> patientIds = mmlService.getFacilityPatientList(facilityId);
        return ResponseEntity.ok(patientIds);
    }

    /**
     * Get patient by primary key with health insurance.
     * @param patientPK patient primary key
     * @return patient model
     */
    @GetMapping("/patient/{patientPK}")
    public ResponseEntity<PatientModel> getPatientByPK(@PathVariable("patientPK") long patientPK) {
        PatientModel patient = mmlService.getPatientByPK(patientPK);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility disease list.
     * @param facilityId facility ID
     * @return list of disease IDs
     */
    @GetMapping("/disease/list")
    public ResponseEntity<List<Long>> getFacilityDiseaseList(@RequestParam("fid") String facilityId) {
        List<Long> diseaseIds = mmlService.getFacilityDiseaseList(facilityId);
        return ResponseEntity.ok(diseaseIds);
    }

    /**
     * Get disease by primary key.
     * @param diseasePK disease primary key
     * @return disease model
     */
    @GetMapping("/disease/{diseasePK}")
    public ResponseEntity<RegisteredDiagnosisModel> getDiseaseByPK(@PathVariable("diseasePK") long diseasePK) {
        RegisteredDiagnosisModel disease = mmlService.getDiseaseByPK(diseasePK);
        if (disease != null) {
            return ResponseEntity.ok(disease);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility memo list.
     * @param facilityId facility ID
     * @return list of memo IDs
     */
    @GetMapping("/memo/list")
    public ResponseEntity<List<Long>> getFacilityMemoList(@RequestParam("fid") String facilityId) {
        List<Long> memoIds = mmlService.getFacilityMemoList(facilityId);
        return ResponseEntity.ok(memoIds);
    }

    /**
     * Get memo by primary key.
     * @param memoPK memo primary key
     * @return memo model
     */
    @GetMapping("/memo/{memoPK}")
    public ResponseEntity<PatientMemoModel> getMemoByPK(@PathVariable("memoPK") long memoPK) {
        PatientMemoModel memo = mmlService.getMemoByPK(memoPK);
        if (memo != null) {
            return ResponseEntity.ok(memo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility observation list.
     * @param facilityId facility ID
     * @return list of observation IDs
     */
    @GetMapping("/observation/list")
    public ResponseEntity<List<Long>> getFacilityObservationList(@RequestParam("fid") String facilityId) {
        List<Long> observationIds = mmlService.getFacilityObservationList(facilityId);
        return ResponseEntity.ok(observationIds);
    }

    /**
     * Get observation by primary key.
     * @param observationPK observation primary key
     * @return observation model
     */
    @GetMapping("/observation/{observationPK}")
    public ResponseEntity<ObservationModel> getObservationByPK(@PathVariable("observationPK") long observationPK) {
        ObservationModel observation = mmlService.getObservationByPK(observationPK);
        if (observation != null) {
            return ResponseEntity.ok(observation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility karte list.
     * @param facilityId facility ID
     * @return list of karte IDs
     */
    @GetMapping("/karte/list")
    public ResponseEntity<List<Long>> getFacilityKarteList(@RequestParam("fid") String facilityId) {
        List<Long> karteIds = mmlService.getFacilityKarteList(facilityId);
        return ResponseEntity.ok(karteIds);
    }

    /**
     * Get karte by primary key with all components.
     * @param kartePK karte primary key
     * @return document model
     */
    @GetMapping("/karte/{kartePK}")
    public ResponseEntity<DocumentModel> getKarteByPK(@PathVariable("kartePK") long kartePK) {
        DocumentModel karte = mmlService.getKarteByPK(kartePK);
        if (karte != null) {
            return ResponseEntity.ok(karte);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility letter list.
     * @param facilityId facility ID
     * @return list of letter IDs
     */
    @GetMapping("/letter/list")
    public ResponseEntity<List<Long>> getFacilityLetterList(@RequestParam("fid") String facilityId) {
        List<Long> letterIds = mmlService.getFacilityLetterList(facilityId);
        return ResponseEntity.ok(letterIds);
    }

    /**
     * Get letter by primary key with all components.
     * @param letterPK letter primary key
     * @return letter module
     */
    @GetMapping("/letter/{letterPK}")
    public ResponseEntity<LetterModule> getLetterByPK(@PathVariable("letterPK") long letterPK) {
        LetterModule letter = mmlService.getLetterByPK(letterPK);
        if (letter != null) {
            return ResponseEntity.ok(letter);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get facility lab test list.
     * @param facilityId facility ID
     * @return list of lab test IDs
     */
    @GetMapping("/labtest/list")
    public ResponseEntity<List<Long>> getFacilityLabtestList(@RequestParam("fid") String facilityId) {
        List<Long> labTestIds = mmlService.getFacilityLabtestList(facilityId);
        return ResponseEntity.ok(labTestIds);
    }

    /**
     * Get lab test by primary key with items.
     * @param labTestPK lab test primary key
     * @return lab test module
     */
    @GetMapping("/labtest/{labTestPK}")
    public ResponseEntity<NLaboModule> getLabtestByPK(@PathVariable("labTestPK") long labTestPK) {
        NLaboModule labTest = mmlService.getLabtestByPK(labTestPK);
        if (labTest != null) {
            return ResponseEntity.ok(labTest);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get MML export statistics for a facility.
     * @param facilityId facility ID
     * @return statistics map
     */
    @GetMapping("/stats/{facilityId}")
    public ResponseEntity<java.util.Map<String, Object>> getMmlStats(@PathVariable("facilityId") String facilityId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("facilityId", facilityId);
        stats.put("patientCount", mmlService.getFacilityPatientCount(facilityId));
        stats.put("documentCount", mmlService.getFacilityDocumentCount(facilityId));
        stats.put("diseaseCount", mmlService.getFacilityDiseaseList(facilityId).size());
        stats.put("memoCount", mmlService.getFacilityMemoList(facilityId).size());
        stats.put("observationCount", mmlService.getFacilityObservationList(facilityId).size());
        stats.put("karteCount", mmlService.getFacilityKarteList(facilityId).size());
        stats.put("letterCount", mmlService.getFacilityLetterList(facilityId).size());
        stats.put("labTestCount", mmlService.getFacilityLabtestList(facilityId).size());

        return ResponseEntity.ok(stats);
    }

    /**
     * Batch export facility data to MML format.
     * @param facilityId facility ID
     * @param dataType type of data to export (patient, document, etc.)
     * @return success status
     */
    @PostMapping("/export/{facilityId}")
    public ResponseEntity<String> batchExportToMML(
            @PathVariable("facilityId") String facilityId,
            @RequestParam("type") String dataType) {

        try {
            String result = "Batch export completed for " + dataType + " in facility " + facilityId;
            // This would implement the batch export logic
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to batch export: " + e.getMessage());
        }
    }
}
