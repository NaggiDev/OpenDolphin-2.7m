package open.dolphin.spring.controller;

import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.spring.service.ChartEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for chart event management and patient visit tracking.
 * Migrated from ChartEventResource (JAX-RS).
 */
@RestController
@RequestMapping("/chart-event")
public class ChartEventController {

    @Autowired
    private ChartEventService chartEventService;

    /**
     * Process a chart event (PVT state changes, deletions, etc.).
     * @param event chart event to process
     * @return processing result
     */
    @PostMapping("/process")
    public ResponseEntity<String> processChartEvent(@RequestBody ChartEventModel event) {
        try {
            int result = chartEventService.processChartEvent(event);
            if (result == 1) {
                return ResponseEntity.ok("Chart event processed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to process chart event");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process chart event: " + e.getMessage());
        }
    }

    /**
     * Get patient visit list for a facility.
     * @param facilityId facility ID
     * @return list of patient visits
     */
    @GetMapping("/pvt-list/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getPvtList(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);
        return ResponseEntity.ok(pvtList);
    }

    /**
     * Get server UUID for this instance.
     * @return server UUID
     */
    @GetMapping("/server-uuid")
    public ResponseEntity<String> getServerUUID() {
        String uuid = chartEventService.getServerUUID();
        return ResponseEntity.ok(uuid);
    }

    /**
     * Notify clients about a chart event.
     * @param event chart event to notify
     * @return notification result
     */
    @PostMapping("/notify")
    public ResponseEntity<String> notifyEvent(@RequestBody ChartEventModel event) {
        try {
            chartEventService.notifyEvent(event);
            return ResponseEntity.ok("Event notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send event notification: " + e.getMessage());
        }
    }

    /**
     * Get patient visit statistics for a facility.
     * @param facilityId facility ID
     * @return statistics map
     */
    @GetMapping("/pvt-stats/{facilityId}")
    public ResponseEntity<java.util.Map<String, Object>> getPvtStats(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("facilityId", facilityId);
        stats.put("totalVisits", pvtList.size());

        // Count by state
        long waiting = pvtList.stream().filter(pvt -> pvt.getState() == 0).count();
        long inProgress = pvtList.stream().filter(pvt -> pvt.getState() >= 2).count();
        long completed = pvtList.stream().filter(pvt -> pvt.getState() == 1).count();

        stats.put("waiting", waiting);
        stats.put("inProgress", inProgress);
        stats.put("completed", completed);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get patient visits by state.
     * @param facilityId facility ID
     * @param state visit state
     * @return list of patient visits with specified state
     */
    @GetMapping("/pvt-by-state/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getPvtByState(
            @PathVariable("facilityId") String facilityId,
            @RequestParam("state") int state) {

        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter by state
        List<PatientVisitModel> filteredList = pvtList.stream()
                .filter(pvt -> pvt.getState() == state)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(filteredList);
    }

    /**
     * Get patient visits with urgent status.
     * @param facilityId facility ID
     * @return list of urgent patient visits
     */
    @GetMapping("/urgent/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getUrgentVisits(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter urgent visits (BIT_HURRY flag)
        List<PatientVisitModel> urgentList = pvtList.stream()
                .filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(urgentList);
    }

    /**
     * Get patient visits that are currently being treated.
     * @param facilityId facility ID
     * @return list of visits in treatment
     */
    @GetMapping("/in-treatment/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getVisitsInTreatment(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter visits in treatment (BIT_TREATMENT flag)
        List<PatientVisitModel> treatmentList = pvtList.stream()
                .filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_TREATMENT)) > 0)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(treatmentList);
    }

    /**
     * Get patient visits that have been cancelled.
     * @param facilityId facility ID
     * @return list of cancelled visits
     */
    @GetMapping("/cancelled/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getCancelledVisits(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter cancelled visits (BIT_CANCEL flag)
        List<PatientVisitModel> cancelledList = pvtList.stream()
                .filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(cancelledList);
    }

    /**
     * Get patient visits that have gone out.
     * @param facilityId facility ID
     * @return list of visits that have gone out
     */
    @GetMapping("/gone-out/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getGoneOutVisits(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter gone out visits (BIT_GO_OUT flag)
        List<PatientVisitModel> goneOutList = pvtList.stream()
                .filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(goneOutList);
    }

    /**
     * Get patient visits by owner UUID.
     * @param facilityId facility ID
     * @param ownerUUID owner UUID
     * @return list of visits owned by specified user
     */
    @GetMapping("/by-owner/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getVisitsByOwner(
            @PathVariable("facilityId") String facilityId,
            @RequestParam("ownerUUID") String ownerUUID) {

        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter by owner UUID
        List<PatientVisitModel> ownerList = pvtList.stream()
                .filter(pvt -> ownerUUID.equals(pvt.getPatientModel().getOwnerUUID()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(ownerList);
    }

    /**
     * Get patient visits with memos.
     * @param facilityId facility ID
     * @return list of visits with memos
     */
    @GetMapping("/with-memos/{facilityId}")
    public ResponseEntity<List<PatientVisitModel>> getVisitsWithMemos(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        // Filter visits with memos
        List<PatientVisitModel> memoList = pvtList.stream()
                .filter(pvt -> pvt.getMemo() != null && !pvt.getMemo().trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(memoList);
    }

    /**
     * Get patient visits dashboard for a facility.
     * @param facilityId facility ID
     * @return dashboard data
     */
    @GetMapping("/dashboard/{facilityId}")
    public ResponseEntity<java.util.Map<String, Object>> getFacilityDashboard(@PathVariable("facilityId") String facilityId) {
        List<PatientVisitModel> pvtList = chartEventService.getPvtList(facilityId);

        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("facilityId", facilityId);
        dashboard.put("serverUUID", chartEventService.getServerUUID());
        dashboard.put("totalVisits", pvtList.size());

        // Count by state
        long waiting = pvtList.stream().filter(pvt -> pvt.getState() == 0).count();
        long inProgress = pvtList.stream().filter(pvt -> pvt.getState() >= 2).count();
        long completed = pvtList.stream().filter(pvt -> pvt.getState() == 1).count();

        dashboard.put("waiting", waiting);
        dashboard.put("inProgress", inProgress);
        dashboard.put("completed", completed);

        // Count by flags
        long urgent = pvtList.stream().filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0).count();
        long cancelled = pvtList.stream().filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0).count();
        long goneOut = pvtList.stream().filter(pvt -> (pvt.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0).count();

        dashboard.put("urgent", urgent);
        dashboard.put("cancelled", cancelled);
        dashboard.put("goneOut", goneOut);

        // Recent visits (last 10)
        List<PatientVisitModel> recentVisits = pvtList.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId())) // Sort by ID descending
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        dashboard.put("recentVisits", recentVisits);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Start the chart event service.
     * @return service start result
     */
    @PostMapping("/start")
    public ResponseEntity<String> startService() {
        try {
            chartEventService.start();
            return ResponseEntity.ok("Chart event service started successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to start chart event service: " + e.getMessage());
        }
    }
}
