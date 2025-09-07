package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.AppointmentModel;
import open.dolphin.spring.service.AppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for appointment management.
 * Migrated from AppoResource (JAX-RS).
 */
@RestController
@RequestMapping("/appo")
public class AppoController {

    @Autowired
    private AppoService appoService;

    /**
     * Save or update appointments with different states.
     * @param appointments list of appointment models
     * @return number of processed appointments
     */
    @PostMapping("/batch")
    public ResponseEntity<String> putAppointments(@RequestBody List<AppointmentModel> appointments) {
        try {
            int processed = appoService.putAppointments(appointments);
            return ResponseEntity.ok("Processed " + processed + " appointments out of " + appointments.size());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process appointments: " + e.getMessage());
        }
    }

    /**
     * Get appointments for a karte within specified date ranges.
     * @param karteId karte ID
     * @param fromDates list of start dates
     * @param toDates list of end dates
     * @return list of appointment lists for each date range
     */
    @GetMapping("/list/{karteId}")
    public ResponseEntity<?> getAppointmentList(
            @PathVariable("karteId") long karteId,
            @RequestParam("fromDates") List<java.util.Date> fromDates,
            @RequestParam("toDates") List<java.util.Date> toDates) {

        try {
            List<List<AppointmentModel>> appointments = appoService.getAppointmentList(karteId, fromDates, toDates);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get appointment list: " + e.getMessage());
        }
    }

    /**
     * Get all appointments for a karte within a single date range.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return list of appointments
     */
    @GetMapping("/range/{karteId}")
    public ResponseEntity<List<AppointmentModel>> getAppointmentsByDateRange(
            @PathVariable("karteId") long karteId,
            @RequestParam("fromDate") java.util.Date fromDate,
            @RequestParam("toDate") java.util.Date toDate) {

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, fromDate, toDate);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointment statistics for a karte.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return statistics map
     */
    @GetMapping("/stats/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getAppointmentStats(
            @PathVariable("karteId") long karteId,
            @RequestParam("fromDate") java.util.Date fromDate,
            @RequestParam("toDate") java.util.Date toDate) {

        java.util.Map<String, Object> stats = appoService.getAppointmentStats(karteId, fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Cancel an appointment by ID.
     * @param appointmentId appointment ID
     * @return success status
     */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable("appointmentId") long appointmentId) {
        try {
            int result = appoService.cancelAppointment(appointmentId);
            if (result == 1) {
                return ResponseEntity.ok("Appointment cancelled successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to cancel appointment: " + e.getMessage());
        }
    }

    /**
     * Update a single appointment.
     * @param appointmentId appointment ID
     * @param appointment updated appointment data
     * @return success status
     */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<String> updateAppointment(
            @PathVariable("appointmentId") long appointmentId,
            @RequestBody AppointmentModel appointment) {

        try {
            appointment.setId(appointmentId);
            int result = appoService.updateAppointment(appointment);
            if (result == 1) {
                return ResponseEntity.ok("Appointment updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update appointment: " + e.getMessage());
        }
    }

    /**
     * Create a new appointment.
     * @param appointment appointment to create
     * @return created appointment ID
     */
    @PostMapping
    public ResponseEntity<Long> createAppointment(@RequestBody AppointmentModel appointment) {
        try {
            long appointmentId = appoService.createAppointment(appointment);
            return ResponseEntity.ok(appointmentId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get appointments by status.
     * @param karteId karte ID
     * @param status appointment status (TT_NEW, TT_REPLACE)
     * @return list of appointments with specified status
     */
    @GetMapping("/status/{karteId}")
    public ResponseEntity<List<AppointmentModel>> getAppointmentsByStatus(
            @PathVariable("karteId") long karteId,
            @RequestParam("status") int status,
            @RequestParam("fromDate") java.util.Date fromDate,
            @RequestParam("toDate") java.util.Date toDate) {

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, fromDate, toDate);

        // Filter by status
        List<AppointmentModel> filteredAppointments = appointments.stream()
                .filter(apt -> apt.getState() == status)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(filteredAppointments);
    }

    /**
     * Get upcoming appointments for a karte.
     * @param karteId karte ID
     * @param limit maximum number of results
     * @return list of upcoming appointments
     */
    @GetMapping("/upcoming/{karteId}")
    public ResponseEntity<List<AppointmentModel>> getUpcomingAppointments(
            @PathVariable("karteId") long karteId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        // Get appointments from today onwards
        java.util.Date today = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, 3); // 3 months from now
        java.util.Date futureDate = cal.getTime();

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, today, futureDate);

        // Sort by date and limit results
        appointments.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        int endIndex = Math.min(limit, appointments.size());
        List<AppointmentModel> upcomingAppointments = appointments.subList(0, endIndex);

        return ResponseEntity.ok(upcomingAppointments);
    }

    /**
     * Get appointment count for a karte within date range.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return count of appointments
     */
    @GetMapping("/count/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getAppointmentCount(
            @PathVariable("karteId") long karteId,
            @RequestParam("fromDate") java.util.Date fromDate,
            @RequestParam("toDate") java.util.Date toDate) {

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, fromDate, toDate);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("karteId", karteId);
        result.put("fromDate", fromDate);
        result.put("toDate", toDate);
        result.put("count", appointments.size());

        return ResponseEntity.ok(result);
    }

    /**
     * Get appointments summary for a karte.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return summary information
     */
    @GetMapping("/summary/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getAppointmentSummary(
            @PathVariable("karteId") long karteId,
            @RequestParam("fromDate") java.util.Date fromDate,
            @RequestParam("toDate") java.util.Date toDate) {

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, fromDate, toDate);

        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("karteId", karteId);
        summary.put("totalAppointments", appointments.size());
        summary.put("fromDate", fromDate);
        summary.put("toDate", toDate);

        // Count by status
        long newAppointments = appointments.stream()
                .filter(apt -> apt.getState() == AppointmentModel.TT_NEW)
                .count();
        long modifiedAppointments = appointments.stream()
                .filter(apt -> apt.getState() == AppointmentModel.TT_REPLACE && apt.getName() != null)
                .count();
        long cancelledAppointments = appointments.stream()
                .filter(apt -> apt.getState() == AppointmentModel.TT_REPLACE && apt.getName() == null)
                .count();

        summary.put("newAppointments", newAppointments);
        summary.put("modifiedAppointments", modifiedAppointments);
        summary.put("cancelledAppointments", cancelledAppointments);

        return ResponseEntity.ok(summary);
    }

    /**
     * Bulk update appointments.
     * @param updates list of appointment updates
     * @return bulk update result
     */
    @PutMapping("/bulk")
    public ResponseEntity<String> bulkUpdateAppointments(@RequestBody List<AppointmentModel> updates) {
        try {
            int processed = appoService.putAppointments(updates);
            return ResponseEntity.ok("Bulk updated " + processed + " appointments out of " + updates.size());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to bulk update appointments: " + e.getMessage());
        }
    }

    /**
     * Get appointment dashboard data for a karte.
     * @param karteId karte ID
     * @return dashboard data
     */
    @GetMapping("/dashboard/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getAppointmentDashboard(@PathVariable("karteId") long karteId) {
        // Get appointments for next 30 days
        java.util.Date today = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
        java.util.Date thirtyDaysFromNow = cal.getTime();

        List<AppointmentModel> appointments = appoService.getAppointments(karteId, today, thirtyDaysFromNow);

        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("karteId", karteId);
        dashboard.put("upcomingAppointments", appointments.size());

        // Group by week
        java.util.Map<String, Integer> weeklyAppointments = new java.util.HashMap<>();
        for (AppointmentModel apt : appointments) {
            java.util.Calendar aptCal = java.util.Calendar.getInstance();
            aptCal.setTime(apt.getDate());
            int weekOfMonth = aptCal.get(java.util.Calendar.WEEK_OF_MONTH);
            String weekKey = "Week " + weekOfMonth;
            weeklyAppointments.put(weekKey, weeklyAppointments.getOrDefault(weekKey, 0) + 1);
        }

        dashboard.put("weeklyBreakdown", weeklyAppointments);
        dashboard.put("appointments", appointments.subList(0, Math.min(10, appointments.size())));

        return ResponseEntity.ok(dashboard);
    }
}
