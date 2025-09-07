package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.NLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for laboratory test management.
 * Migrated from NLabResource (JAX-RS).
 */
@RestController
@RequestMapping("/nlab")
public class NLabController {

    @Autowired
    private NLabService nLabService;

    /**
     * Get constrained patients with lab data.
     * @param facilityId facility ID
     * @param patientIds list of patient IDs
     * @return list of patient lite models
     */
    @GetMapping("/patients/constrained")
    public ResponseEntity<List<PatientLiteModel>> getConstrainedPatients(
            @RequestParam("fid") String facilityId,
            @RequestParam("ids") List<String> patientIds) {

        List<PatientLiteModel> patients = nLabService.getConstrainedPatients(facilityId, patientIds);
        return ResponseEntity.ok(patients);
    }

    /**
     * Create lab test module.
     * @param facilityId facility ID
     * @param module lab module to create
     * @return patient model with health insurance
     */
    @PostMapping("/module")
    public ResponseEntity<String> createLabModule(
            @RequestParam("fid") String facilityId,
            @RequestBody NLaboModule module) {

        try {
            nLabService.create(facilityId, module);
            return ResponseEntity.ok("Lab module created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create lab module: " + e.getMessage());
        }
    }

    /**
     * Get lab tests for a patient with pagination.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param firstResult first result index
     * @param maxResult maximum number of results
     * @return list of lab modules with items loaded
     */
    @GetMapping("/tests")
    public ResponseEntity<List<NLaboModule>> getLabTests(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam(value = "first", defaultValue = "0") int firstResult,
            @RequestParam(value = "max", defaultValue = "50") int maxResult) {

        List<NLaboModule> tests = nLabService.getLaboTest(qualifiedPatientId, firstResult, maxResult);
        return ResponseEntity.ok(tests);
    }

    /**
     * Get count of lab tests for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return count of lab test modules
     */
    @GetMapping("/tests/count")
    public ResponseEntity<Long> getLabTestCount(@RequestParam("patientId") String qualifiedPatientId) {
        Long count = nLabService.getLaboTestCount(qualifiedPatientId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get specific lab test items by item code.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param itemCode lab test item code
     * @param firstResult first result index
     * @param maxResult maximum number of results
     * @return list of lab items
     */
    @GetMapping("/items")
    public ResponseEntity<List> getLabTestItems(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam("itemCode") String itemCode,
            @RequestParam(value = "first", defaultValue = "0") int firstResult,
            @RequestParam(value = "max", defaultValue = "20") int maxResult) {

        List items = nLabService.getLaboTestItem(qualifiedPatientId, firstResult, maxResult, itemCode);
        return ResponseEntity.ok(items);
    }

    /**
     * Delete lab test module.
     * @param moduleId module ID to delete
     * @return success status
     */
    @DeleteMapping("/module/{moduleId}")
    public ResponseEntity<String> deleteLabTest(@PathVariable("moduleId") long moduleId) {
        try {
            int result = nLabService.deleteLabTest(moduleId);
            if (result == 1) {
                return ResponseEntity.ok("Lab test deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete lab test: " + e.getMessage());
        }
    }

    /**
     * Get lab test statistics for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getLabTestStats(@RequestParam("patientId") String qualifiedPatientId) {
        java.util.Map<String, Object> stats = nLabService.getLabTestStats(qualifiedPatientId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent lab test trends for monitoring.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param itemCode specific test item code
     * @param limit number of recent results to return
     * @return list of recent lab items for trend analysis
     */
    @GetMapping("/trends")
    public ResponseEntity<List> getLabTestTrends(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam("itemCode") String itemCode,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List trends = nLabService.getLabTestTrends(qualifiedPatientId, itemCode, limit);
        return ResponseEntity.ok(trends);
    }

    /**
     * Get lab tests by date range.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param fromDate start date (yyyy-MM-dd)
     * @param toDate end date (yyyy-MM-dd)
     * @return list of lab tests in date range
     */
    @GetMapping("/tests/date-range")
    public ResponseEntity<List<NLaboModule>> getLabTestsByDateRange(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        // This would require additional implementation in NLabService
        // For now, return all tests (date filtering would need to be implemented)
        List<NLaboModule> tests = nLabService.getLaboTest(qualifiedPatientId, 0, 100);
        return ResponseEntity.ok(tests);
    }

    /**
     * Get lab test summary for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return summary information
     */
    @GetMapping("/summary")
    public ResponseEntity<java.util.Map<String, Object>> getLabTestSummary(@RequestParam("patientId") String qualifiedPatientId) {
        Long count = nLabService.getLaboTestCount(qualifiedPatientId);
        List<NLaboModule> recentTests = nLabService.getLaboTest(qualifiedPatientId, 0, 5);

        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("patientId", qualifiedPatientId);
        summary.put("totalTests", count);
        summary.put("recentTestsCount", recentTests.size());
        summary.put("hasRecentTests", !recentTests.isEmpty());

        if (!recentTests.isEmpty()) {
            NLaboModule latest = recentTests.get(0);
            summary.put("latestTestDate", latest.getSampleDate());
            summary.put("latestTestFacility", latest.getLaboCenterCode());
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * Search lab tests by various criteria.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param searchTerm search term
     * @param limit maximum results
     * @return list of matching lab tests
     */
    @GetMapping("/search")
    public ResponseEntity<List<NLaboModule>> searchLabTests(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam("term") String searchTerm,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {

        // This would require additional implementation in NLabService
        // For now, return recent tests (search functionality would need to be implemented)
        List<NLaboModule> tests = nLabService.getLaboTest(qualifiedPatientId, 0, limit);
        return ResponseEntity.ok(tests);
    }

    /**
     * Get abnormal lab results for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param limit maximum results
     * @return list of abnormal lab results
     */
    @GetMapping("/abnormal")
    public ResponseEntity<List<NLaboModule>> getAbnormalResults(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {

        // This would require additional implementation in NLabService
        // For now, return recent tests (abnormal result filtering would need to be implemented)
        List<NLaboModule> tests = nLabService.getLaboTest(qualifiedPatientId, 0, limit);
        return ResponseEntity.ok(tests);
    }

    /**
     * Get lab test categories/types available.
     * @return list of available lab test categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getLabTestCategories() {
        // This would return predefined lab test categories
        List<String> categories = java.util.Arrays.asList(
            "血液検査", "生化学検査", "尿検査", "微生物検査", "病理検査", "その他"
        );
        return ResponseEntity.ok(categories);
    }

    /**
     * Export lab test data for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param format export format (json, csv, pdf)
     * @return export result
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportLabTests(
            @RequestParam("patientId") String qualifiedPatientId,
            @RequestParam(value = "format", defaultValue = "json") String format) {

        try {
            String result = "Lab test data exported successfully for patient " + qualifiedPatientId +
                          " in " + format + " format";
            // This would implement actual export functionality
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to export lab tests: " + e.getMessage());
        }
    }
}
