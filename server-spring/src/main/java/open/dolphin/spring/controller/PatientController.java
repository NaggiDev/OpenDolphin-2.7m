package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.PatientModel;
import open.dolphin.spring.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for patient operations.
 * Migrated from PatientResource (JAX-RS).
 */
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Search patients by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<PatientModel>> getPatientsByName(
            @RequestParam("fid") String fid,
            @PathVariable("name") String name) {

        List<PatientModel> patients = patientService.getPatientsByName(fid, name);
        return ResponseEntity.ok(patients);
    }

    /**
     * Search patients by kana name
     */
    @GetMapping("/kana/{name}")
    public ResponseEntity<List<PatientModel>> getPatientsByKana(
            @RequestParam("fid") String fid,
            @PathVariable("name") String name) {

        List<PatientModel> patients = patientService.getPatientsByKana(fid, name);
        return ResponseEntity.ok(patients);
    }

    /**
     * Search patients by digit (ID, phone, zip code)
     */
    @GetMapping("/digit/{digit}")
    public ResponseEntity<List<PatientModel>> getPatientsByDigit(
            @RequestParam("fid") String fid,
            @PathVariable("digit") String digit) {

        List<PatientModel> patients = patientService.getPatientsByDigit(fid, digit);
        return ResponseEntity.ok(patients);
    }

    /**
     * Get patient by ID
     */
    @GetMapping("/id/{patientId}")
    public ResponseEntity<PatientModel> getPatientById(
            @RequestParam("fid") String fid,
            @PathVariable("patientId") String patientId) {

        PatientModel patient = patientService.getPatientById(fid, patientId);
        return ResponseEntity.ok(patient);
    }

    /**
     * Get patients by visit date
     */
    @GetMapping("/pvt/{date}")
    public ResponseEntity<List<PatientModel>> getPatientsByPvtDate(
            @RequestParam("fid") String fid,
            @PathVariable("date") String date) {

        List<PatientModel> patients = patientService.getPatientsByPvtDate(fid, date);
        return ResponseEntity.ok(patients);
    }

    /**
     * Get temporary karte patients
     */
    @GetMapping("/documents/status")
    public ResponseEntity<List<PatientModel>> getTmpKartePatients(
            @RequestParam("fid") String fid) {

        List<PatientModel> patients = patientService.getTmpKarte(fid);
        return ResponseEntity.ok(patients);
    }

    /**
     * Add new patient
     */
    @PostMapping
    public ResponseEntity<Long> addPatient(
            @RequestParam("fid") String fid,
            @RequestBody PatientModel patient) {

        patient.setFacilityId(fid);
        long patientId = patientService.addPatient(patient);
        return ResponseEntity.ok(patientId);
    }

    /**
     * Update patient
     */
    @PutMapping
    public ResponseEntity<Integer> updatePatient(
            @RequestParam("fid") String fid,
            @RequestBody PatientModel patient) {

        patient.setFacilityId(fid);
        int result = patientService.update(patient);
        return ResponseEntity.ok(result);
    }

    /**
     * Get patient count for search validation
     */
    @GetMapping("/count/{patientId}")
    public ResponseEntity<Long> getPatientCount(
            @RequestParam("fid") String fid,
            @PathVariable("patientId") String patientId) {

        Long count = patientService.getPatientCount(fid, patientId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get all patients (for bulk operations)
     */
    @GetMapping("/all")
    public ResponseEntity<List<PatientModel>> getAllPatients(
            @RequestParam("fid") String fid) {

        List<PatientModel> patients = patientService.getAllPatient(fid);
        return ResponseEntity.ok(patients);
    }

    /**
     * Custom search (by diagnosis)
     */
    @GetMapping("/custom/{param}")
    public ResponseEntity<List<PatientModel>> getPatientsByCustomSearch(
            @RequestParam("fid") String fid,
            @PathVariable("param") String param) {

        List<PatientModel> patients = patientService.getCustom(fid, param);
        return ResponseEntity.ok(patients);
    }
}
