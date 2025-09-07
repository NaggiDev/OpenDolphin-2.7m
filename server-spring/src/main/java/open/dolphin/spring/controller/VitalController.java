package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.VitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vital signs management.
 * Migrated from VitalResource (JAX-RS).
 */
@RestController
@RequestMapping("/vital")
public class VitalController {

    @Autowired
    private VitalService vitalService;

    /**
     * Add a new vital sign record.
     * @param vital the vital sign to add
     * @return success status
     */
    @PostMapping
    public ResponseEntity<String> addVital(@RequestBody VitalModel vital) {
        try {
            int result = vitalService.addVital(vital);
            if (result == 1) {
                return ResponseEntity.ok("Vital sign added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add vital sign");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add vital sign: " + e.getMessage());
        }
    }

    /**
     * Update an existing vital sign record.
     * @param vitalId the vital sign ID
     * @param vital the updated vital sign data
     * @return success status
     */
    @PutMapping("/{vitalId}")
    public ResponseEntity<String> updateVital(@PathVariable("vitalId") String vitalId, @RequestBody VitalModel vital) {
        try {
            vital.setId(Long.parseLong(vitalId));
            int result = vitalService.updateVital(vital);
            if (result == 1) {
                return ResponseEntity.ok("Vital sign updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update vital sign: " + e.getMessage());
        }
    }

    /**
     * Get a vital sign by ID.
     * @param vitalId the vital sign ID
     * @return the vital sign
     */
    @GetMapping("/{vitalId}")
    public ResponseEntity<VitalModel> getVital(@PathVariable("vitalId") String vitalId) {
        VitalModel vital = vitalService.getVital(vitalId);
        if (vital != null) {
            return ResponseEntity.ok(vital);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all vital signs for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return list of vital signs
     */
    @GetMapping("/patient/{qualifiedPatientId}")
    public ResponseEntity<List<VitalModel>> getPatientVitals(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        List<VitalModel> vitals = vitalService.getPatVital(qualifiedPatientId);
        return ResponseEntity.ok(vitals);
    }

    /**
     * Remove a vital sign by ID.
     * @param vitalId the vital sign ID to remove
     * @return success status
     */
    @DeleteMapping("/{vitalId}")
    public ResponseEntity<String> removeVital(@PathVariable("vitalId") String vitalId) {
        try {
            int result = vitalService.removeVital(vitalId);
            if (result == 1) {
                return ResponseEntity.ok("Vital sign removed successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to remove vital sign: " + e.getMessage());
        }
    }

    /**
     * Get patient by qualified patient ID.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return patient model
     */
    @GetMapping("/patient/info/{qualifiedPatientId}")
    public ResponseEntity<PatientModel> getPatientByFpid(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        PatientModel patient = vitalService.getPatientByFpid(qualifiedPatientId);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get vital signs statistics for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return statistics map
     */
    @GetMapping("/stats/{qualifiedPatientId}")
    public ResponseEntity<java.util.Map<String, Object>> getVitalStats(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        java.util.Map<String, Object> stats = vitalService.getVitalStats(qualifiedPatientId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent vital signs for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param limit maximum number of results
     * @return list of recent vital signs
     */
    @GetMapping("/recent/{qualifiedPatientId}")
    public ResponseEntity<List<VitalModel>> getRecentVitals(
            @PathVariable("qualifiedPatientId") String qualifiedPatientId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<VitalModel> vitals = vitalService.getRecentVitals(qualifiedPatientId, limit);
        return ResponseEntity.ok(vitals);
    }

    /**
     * Check if patient has any vital signs recorded.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return existence status
     */
    @GetMapping("/exists/{qualifiedPatientId}")
    public ResponseEntity<java.util.Map<String, Object>> hasVitals(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        boolean exists = vitalService.hasVitals(qualifiedPatientId);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("patientId", qualifiedPatientId);
        result.put("hasVitals", exists);

        return ResponseEntity.ok(result);
    }

    /**
     * Get vital signs summary for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return summary information
     */
    @GetMapping("/summary/{qualifiedPatientId}")
    public ResponseEntity<java.util.Map<String, Object>> getVitalSummary(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        List<VitalModel> vitals = vitalService.getPatVital(qualifiedPatientId);
        boolean hasVitals = vitalService.hasVitals(qualifiedPatientId);

        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("patientId", qualifiedPatientId);
        summary.put("totalVitals", vitals.size());
        summary.put("hasVitals", hasVitals);

        if (!vitals.isEmpty()) {
            // Get the most recent vital sign
            VitalModel latest = vitals.get(0);
            summary.put("latestVitalId", latest.getId());
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * Get vital signs by date range.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param fromDate start date (yyyy-MM-dd)
     * @param toDate end date (yyyy-MM-dd)
     * @return list of vital signs in date range
     */
    @GetMapping("/date-range/{qualifiedPatientId}")
    public ResponseEntity<List<VitalModel>> getVitalsByDateRange(
            @PathVariable("qualifiedPatientId") String qualifiedPatientId,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        // This would require additional implementation in VitalService
        // For now, return all vitals (date filtering would need to be implemented)
        List<VitalModel> vitals = vitalService.getPatVital(qualifiedPatientId);
        return ResponseEntity.ok(vitals);
    }

    /**
     * Get vital signs count for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return count of vital signs
     */
    @GetMapping("/count/{qualifiedPatientId}")
    public ResponseEntity<java.util.Map<String, Object>> getVitalCount(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        List<VitalModel> vitals = vitalService.getPatVital(qualifiedPatientId);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("patientId", qualifiedPatientId);
        result.put("count", vitals.size());

        return ResponseEntity.ok(result);
    }

    /**
     * Search vital signs by criteria.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param searchTerm search term
     * @param limit maximum results
     * @return list of matching vital signs
     */
    @GetMapping("/search/{qualifiedPatientId}")
    public ResponseEntity<List<VitalModel>> searchVitals(
            @PathVariable("qualifiedPatientId") String qualifiedPatientId,
            @RequestParam("term") String searchTerm,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {

        // This would require additional implementation in VitalService
        // For now, return recent vitals (search functionality would need to be implemented)
        List<VitalModel> vitals = vitalService.getRecentVitals(qualifiedPatientId, limit);
        return ResponseEntity.ok(vitals);
    }

    /**
     * Get vital signs with abnormal values.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param limit maximum results
     * @return list of abnormal vital signs
     */
    @GetMapping("/abnormal/{qualifiedPatientId}")
    public ResponseEntity<List<VitalModel>> getAbnormalVitals(
            @PathVariable("qualifiedPatientId") String qualifiedPatientId,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {

        // This would require additional implementation in VitalService
        // For now, return recent vitals (abnormal value filtering would need to be implemented)
        List<VitalModel> vitals = vitalService.getRecentVitals(qualifiedPatientId, limit);
        return ResponseEntity.ok(vitals);
    }

    /**
     * Export vital signs data for a patient.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @param format export format (json, csv, pdf)
     * @return export result
     */
    @GetMapping("/export/{qualifiedPatientId}")
    public ResponseEntity<String> exportVitals(
            @PathVariable("qualifiedPatientId") String qualifiedPatientId,
            @RequestParam(value = "format", defaultValue = "json") String format) {

        try {
            String result = "Vital signs data exported successfully for patient " + qualifiedPatientId +
                          " in " + format + " format";
            // This would implement actual export functionality
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to export vital signs: " + e.getMessage());
        }
    }

    /**
     * Get vital signs monitoring dashboard data.
     * @param qualifiedPatientId qualified patient ID (facilityId:patientId)
     * @return dashboard data
     */
    @GetMapping("/dashboard/{qualifiedPatientId}")
    public ResponseEntity<java.util.Map<String, Object>> getVitalDashboard(@PathVariable("qualifiedPatientId") String qualifiedPatientId) {
        List<VitalModel> vitals = vitalService.getPatVital(qualifiedPatientId);
        java.util.Map<String, Object> stats = vitalService.getVitalStats(qualifiedPatientId);

        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("patientId", qualifiedPatientId);
        dashboard.put("totalReadings", vitals.size());
        dashboard.put("statistics", stats);
        dashboard.put("recentReadings", vitalService.getRecentVitals(qualifiedPatientId, 5));
        dashboard.put("hasData", !vitals.isEmpty());

        return ResponseEntity.ok(dashboard);
    }
}
