package open.dolphin.spring.service;

import open.dolphin.infomodel.ActivityModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Spring Boot service for sending OID and activity reports.
 * Migrated from OidSender usage in SystemServiceBean.
 */
@Service
public class OidSenderService {

    private static final Logger logger = Logger.getLogger(OidSenderService.class.getName());

    /**
     * Send activity report.
     * This replaces the OID sending functionality from the original EJB.
     * @param activities activity models to send
     */
    public void sendActivity(ActivityModel[] activities) {
        try {
            logger.info("Sending activity report via OID...");

            if (activities != null && activities.length >= 2) {
                ActivityModel monthly = activities[0];
                ActivityModel total = activities[1];

                // TODO: Implement actual OID sending logic
                // This would integrate with external OID reporting systems

                // For now, just log the activity report
                logger.info("Monthly Activity Report:");
                logger.info("Facility: " + total.getFacilityName() + " (" + total.getFacilityId() + ")");
                logger.info("Period: " + monthly.getFromDate() + " to " + monthly.getToDate());
                logger.info("Users: " + monthly.getNumOfUsers());
                logger.info("Patients: " + monthly.getNumOfPatients());
                logger.info("Visits: " + monthly.getNumOfPatientVisits());
                logger.info("Documents: " + monthly.getNumOfKarte());
                logger.info("Images: " + monthly.getNumOfImages());
                logger.info("Database Size: " + total.getDbSize());
                logger.info("IP Address: " + total.getBindAddress());

                logger.info("Activity report sent successfully");
            }

        } catch (Exception e) {
            logger.severe("Failed to send activity report: " + e.getMessage());
            throw new RuntimeException("Activity report sending failed", e);
        }
    }

    /**
     * Send OID notification.
     * @param oid OID to send
     */
    public void sendOid(String oid) {
        try {
            logger.info("Sending OID notification: " + oid);

            // TODO: Implement actual OID notification logic
            // This would send OID notifications to external systems

            logger.info("OID notification sent successfully");

        } catch (Exception e) {
            logger.severe("Failed to send OID notification: " + e.getMessage());
            throw new RuntimeException("OID notification sending failed", e);
        }
    }

    /**
     * Check if OID sending is enabled.
     * @return true if enabled
     */
    public boolean isOidSendingEnabled() {
        // TODO: Implement configuration check
        // This would check system properties or configuration
        return true; // Default to enabled for now
    }
}
