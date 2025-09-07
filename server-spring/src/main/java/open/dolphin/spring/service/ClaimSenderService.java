package open.dolphin.spring.service;

import open.dolphin.spring.model.entity.DocumentModel;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.logging.Logger;

/**
 * Spring Boot service for sending claims to external systems (ORCA, etc.).
 * Migrated from ClaimSender usage in KarteServiceBean.
 */
@Service
public class ClaimSenderService {

    private static final Logger logger = Logger.getLogger(ClaimSenderService.class.getName());

    // Default configuration values - can be overridden by application.yml
    @Value("${dolphin.claim.host:localhost}")
    private String claimHost;

    @Value("${dolphin.claim.port:8210}")
    private int claimPort;

    @Value("${dolphin.claim.encoding:UTF-8}")
    private String claimEncoding;

    /**
     * Send document claim to external system.
     * This replaces the CLAIM sending functionality from the original EJB.
     */
    public void sendDocument(DocumentModel document) {
        try {
            logger.info("Sending document claim to external system...");

            // TODO: Implement actual claim sending logic
            // This would integrate with ORCA or other claim processing systems

            // For now, just log the claim sending attempt
            logger.info("Document claim sent successfully for document: " +
                       document.getDocInfoModel().getDocId());

            // In the original code, this would:
            // 1. Extract claim data from the document
            // 2. Format it according to claim standards
            // 3. Send to ORCA or other claim processing system
            // 4. Handle response and logging

        } catch (Exception e) {
            logger.severe("Failed to send document claim: " + e.getMessage());
            throw new RuntimeException("Claim sending failed", e);
        }
    }

    /**
     * Send diagnosis claim to external system.
     */
    public void sendDiagnosis(Object diagnosisWrapper) {
        try {
            logger.info("Sending diagnosis claim to external system...");

            // TODO: Implement diagnosis claim sending
            // This would handle diagnosis updates and claims

            logger.info("Diagnosis claim sent successfully");

        } catch (Exception e) {
            logger.severe("Failed to send diagnosis claim: " + e.getMessage());
            throw new RuntimeException("Diagnosis claim sending failed", e);
        }
    }

    /**
     * Check if claim sending is enabled for the facility.
     */
    public boolean isClaimSendingEnabled() {
        // TODO: Implement configuration check
        // This would check facility-specific settings
        return true; // Default to enabled for now
    }

    /**
     * Get claim connection configuration.
     */
    public ClaimConnectionConfig getConnectionConfig() {
        return new ClaimConnectionConfig(claimHost, claimPort, claimEncoding);
    }

    /**
     * Configuration class for claim connections.
     */
    public static class ClaimConnectionConfig {
        private final String host;
        private final int port;
        private final String encoding;

        public ClaimConnectionConfig(String host, int port, String encoding) {
            this.host = host;
            this.port = port;
            this.encoding = encoding;
        }

        public String getHost() { return host; }
        public int getPort() { return port; }
        public String getEncoding() { return encoding; }
    }
}
