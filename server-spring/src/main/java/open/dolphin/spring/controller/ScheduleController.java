package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.PatientVisitModel;
import open.dolphin.spring.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * REST controller for schedule management and patient visit operations.
 * Migrated from ScheduleResource (JAX-RS).
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * Get patient visits for a facility, doctor, and date.
     * @param facilityId facility ID
     * @param doctorId doctor ID (optional)
     * @param unassigned unassigned doctor ID (optional)
     * @param date date string (yyyy-MM-dd)
     * @return list of patient visits
     */
    @GetMapping("/pvt")
    public ResponseEntity<List<PatientVisitModel>> getPatientVisits(
            @RequestParam("fid") String facilityId,
            @RequestParam(value = "did", required = false) String doctorId,
            @RequestParam(value = "unassigned", required = false) String unassigned,
            @RequestParam("date") String date) {

        List<PatientVisitModel> visits = scheduleService.getPvt(facilityId, doctorId, unassigned, date);
        return ResponseEntity.ok(visits);
    }

    /**
     * Create scheduled medical document and optionally send claim.
     * @param pvtPK patient visit primary key
     * @param userPK user primary key
     * @param startDate appointment date
     * @param sendClaim whether to send claim
     * @return success status
     */
    @PostMapping("/document")
    public ResponseEntity<String> makeScheduleAndSend(
            @RequestParam("pvtPK") long pvtPK,
            @RequestParam("userPK") long userPK,
            @RequestParam("startDate") Date startDate,
            @RequestParam(value = "sendClaim", defaultValue = "false") boolean sendClaim) {

        int result = scheduleService.makeScheduleAndSend(pvtPK, userPK, startDate, sendClaim);

        if (result == 1) {
            return ResponseEntity.ok("Scheduled document created successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to create scheduled document");
        }
    }

    /**
     * Remove patient visit and associated documents.
     * @param pvtPK patient visit primary key
     * @param patientPK patient primary key
     * @param startDate appointment date
     * @return number of deleted items
     */
    @DeleteMapping("/pvt")
    public ResponseEntity<String> removePatientVisit(
            @RequestParam("pvtPK") long pvtPK,
            @RequestParam("ptPK") long patientPK,
            @RequestParam("startDate") Date startDate) {

        int deletedCount = scheduleService.removePvt(pvtPK, patientPK, startDate);
        return ResponseEntity.ok("Deleted " + deletedCount + " items");
    }

    /**
     * Delete document with cascade operations.
     * @param documentId document ID to delete
     * @return list of deleted document IDs
     */
    @DeleteMapping("/document/{id}")
    public ResponseEntity<List<String>> deleteDocument(@PathVariable("id") long documentId) {
        List<String> deletedIds = scheduleService.deleteDocument(documentId);
        return ResponseEntity.ok(deletedIds);
    }

    /**
     * Get schedule statistics for a user.
     * @param userPK user primary key
     * @return statistics map
     */
    @GetMapping("/stats/{userPK}")
    public ResponseEntity<java.util.Map<String, Object>> getScheduleStats(@PathVariable("userPK") long userPK) {
        // This would require additional implementation in ScheduleService
        // For now, return a placeholder response
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("userPK", userPK);
        stats.put("message", "Schedule statistics not yet implemented");
        return ResponseEntity.ok(stats);
    }

    /**
     * Get patient visit statistics for a facility and date.
     * @param facilityId facility ID
     * @param date date string
     * @return statistics map
     */
    @GetMapping("/pvt/stats")
    public ResponseEntity<java.util.Map<String, Object>> getPatientVisitStats(
            @RequestParam("fid") String facilityId,
            @RequestParam("date") String date) {

        List<PatientVisitModel> visits = scheduleService.getPvt(facilityId, null, null, date);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("facilityId", facilityId);
        stats.put("date", date);
        stats.put("totalVisits", visits.size());

        // Count visits by doctor
        java.util.Map<String, Integer> doctorStats = new java.util.HashMap<>();
        for (PatientVisitModel visit : visits) {
            String doctorId = visit.getDoctorId() != null ? visit.getDoctorId() : "unassigned";
            doctorStats.put(doctorId, doctorStats.getOrDefault(doctorId, 0) + 1);
        }
        stats.put("doctorStats", doctorStats);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent patient visits for a facility.
     * @param facilityId facility ID
     * @param date date string
     * @param limit maximum number of results
     * @return list of recent visits
     */
    @GetMapping("/pvt/recent")
    public ResponseEntity<List<PatientVisitModel>> getRecentPatientVisits(
            @RequestParam("fid") String facilityId,
            @RequestParam("date") String date,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<PatientVisitModel> visits = scheduleService.getPvt(facilityId, null, null, date);

        // Return only the most recent visits up to the limit
        int endIndex = Math.min(limit, visits.size());
        List<PatientVisitModel> recentVisits = visits.subList(0, endIndex);

        return ResponseEntity.ok(recentVisits);
    }

    /**
     * Get patient visits by doctor for a specific date.
     * @param facilityId facility ID
     * @param doctorId doctor ID
     * @param date date string
     * @return list of patient visits for the doctor
     */
    @GetMapping("/pvt/doctor/{doctorId}")
    public ResponseEntity<List<PatientVisitModel>> getPatientVisitsByDoctor(
            @RequestParam("fid") String facilityId,
            @PathVariable("doctorId") String doctorId,
            @RequestParam("date") String date) {

        List<PatientVisitModel> visits = scheduleService.getPvt(facilityId, doctorId, null, date);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get unassigned patient visits for a specific date.
     * @param facilityId facility ID
     * @param unassigned unassigned doctor ID
     * @param date date string
     * @return list of unassigned patient visits
     */
    @GetMapping("/pvt/unassigned")
    public ResponseEntity<List<PatientVisitModel>> getUnassignedPatientVisits(
            @RequestParam("fid") String facilityId,
            @RequestParam("unassigned") String unassigned,
            @RequestParam("date") String date) {

        List<PatientVisitModel> visits = scheduleService.getPvt(facilityId, null, unassigned, date);
        return ResponseEntity.ok(visits);
    }
}
