package open.dolphin.spring.service;

import open.dolphin.spring.model.entity.AppointmentModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for appointment management.
 * Migrated from AppoServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class AppoService {

    private static final Logger logger = Logger.getLogger(AppoService.class.getName());

    private static final String QUERY_APPOINTMENT_BY_KARTE_ID = "from AppointmentModel a where a.karte.id=:karteId and a.date between :fromDate and :toDate";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";

    @PersistenceContext
    private EntityManager em;

    /**
     * Save or update appointments with different states.
     * @param appointments list of appointment models
     * @return number of processed appointments
     */
    public int putAppointments(List<AppointmentModel> appointments) {
        int count = 0;

        for (AppointmentModel appointment : appointments) {
            try {
                int state = appointment.getState();
                String appointmentName = appointment.getName();

                if (state == AppointmentModel.TT_NEW) {
                    // New appointment
                    em.persist(appointment);
                    logger.info("New appointment created: " + appointment.getId());
                    count++;

                } else if (state == AppointmentModel.TT_REPLACE && appointmentName != null) {
                    // Modified appointment
                    em.merge(appointment);
                    logger.info("Appointment updated: " + appointment.getId());
                    count++;

                } else if (state == AppointmentModel.TT_REPLACE && appointmentName == null) {
                    // Cancelled appointment
                    AppointmentModel target = em.find(AppointmentModel.class, appointment.getId());
                    if (target != null) {
                        em.remove(target);
                        logger.info("Appointment cancelled: " + appointment.getId());
                        count++;
                    } else {
                        logger.warning("Appointment not found for cancellation: " + appointment.getId());
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to process appointment: " + appointment.getId(), e);
            }
        }

        logger.info("Processed " + count + " appointments out of " + appointments.size());
        return count;
    }

    /**
     * Get appointments for a karte within specified date ranges.
     * @param karteId karte ID
     * @param fromDates list of start dates
     * @param toDates list of end dates
     * @return list of appointment lists for each date range
     */
    public List<List<AppointmentModel>> getAppointmentList(long karteId, List<java.util.Date> fromDates, List<java.util.Date> toDates) {
        if (fromDates == null || toDates == null || fromDates.size() != toDates.size()) {
            logger.warning("Invalid date range parameters");
            return new ArrayList<>();
        }

        int length = fromDates.size();
        List<List<AppointmentModel>> result = new ArrayList<>(length);

        // Search for each date range and add to result
        for (int i = 0; i < length; i++) {
            try {
                List<AppointmentModel> appointments = em.createQuery(QUERY_APPOINTMENT_BY_KARTE_ID, AppointmentModel.class)
                                                       .setParameter(KARTE_ID, karteId)
                                                       .setParameter(FROM_DATE, fromDates.get(i))
                                                       .setParameter(TO_DATE, toDates.get(i))
                                                       .getResultList();
                result.add(appointments);
                logger.info("Found " + appointments.size() + " appointments for date range " + (i + 1));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to get appointments for date range " + (i + 1), e);
                result.add(new ArrayList<>()); // Add empty list for failed range
            }
        }

        return result;
    }

    /**
     * Get all appointments for a karte within a single date range.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return list of appointments
     */
    public List<AppointmentModel> getAppointments(long karteId, java.util.Date fromDate, java.util.Date toDate) {
        try {
            List<AppointmentModel> appointments = em.createQuery(QUERY_APPOINTMENT_BY_KARTE_ID, AppointmentModel.class)
                                                   .setParameter(KARTE_ID, karteId)
                                                   .setParameter(FROM_DATE, fromDate)
                                                   .setParameter(TO_DATE, toDate)
                                                   .getResultList();
            logger.info("Found " + appointments.size() + " appointments for karte: " + karteId);
            return appointments;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get appointments for karte: " + karteId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get appointment statistics for a karte.
     * @param karteId karte ID
     * @param fromDate start date
     * @param toDate end date
     * @return statistics map
     */
    public java.util.Map<String, Object> getAppointmentStats(long karteId, java.util.Date fromDate, java.util.Date toDate) {
        List<AppointmentModel> appointments = getAppointments(karteId, fromDate, toDate);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalAppointments", appointments.size());
        stats.put("karteId", karteId);
        stats.put("fromDate", fromDate);
        stats.put("toDate", toDate);

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

        stats.put("newAppointments", newAppointments);
        stats.put("modifiedAppointments", modifiedAppointments);
        stats.put("cancelledAppointments", cancelledAppointments);

        return stats;
    }

    /**
     * Cancel an appointment by ID.
     * @param appointmentId appointment ID
     * @return 1 if successful, 0 if not found
     */
    public int cancelAppointment(long appointmentId) {
        try {
            AppointmentModel appointment = em.find(AppointmentModel.class, appointmentId);
            if (appointment != null) {
                em.remove(appointment);
                logger.info("Appointment cancelled: " + appointmentId);
                return 1;
            } else {
                logger.warning("Appointment not found for cancellation: " + appointmentId);
                return 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to cancel appointment: " + appointmentId, e);
            return 0;
        }
    }

    /**
     * Update a single appointment.
     * @param appointment appointment to update
     * @return 1 if successful, 0 if not found
     */
    public int updateAppointment(AppointmentModel appointment) {
        try {
            AppointmentModel existing = em.find(AppointmentModel.class, appointment.getId());
            if (existing != null) {
                em.merge(appointment);
                logger.info("Appointment updated: " + appointment.getId());
                return 1;
            } else {
                logger.warning("Appointment not found for update: " + appointment.getId());
                return 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update appointment: " + appointment.getId(), e);
            return 0;
        }
    }

    /**
     * Create a new appointment.
     * @param appointment appointment to create
     * @return 1 if successful
     */
    public int createAppointment(AppointmentModel appointment) {
        try {
            em.persist(appointment);
            logger.info("Appointment created: " + appointment.getId());
            return 1;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create appointment", e);
            return 0;
        }
    }
}
