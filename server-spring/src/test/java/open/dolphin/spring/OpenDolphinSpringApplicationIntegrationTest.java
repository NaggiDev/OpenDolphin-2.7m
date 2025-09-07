package open.dolphin.spring;

import open.dolphin.spring.service.PatientService;
import open.dolphin.spring.service.PVTService;
import open.dolphin.spring.service.KarteService;
import open.dolphin.spring.service.ChartEventService;
import open.dolphin.spring.service.ClaimSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the complete Spring Boot application context.
 * Tests that all services are properly wired and the application starts correctly.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OpenDolphinSpringApplicationIntegrationTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PVTService pvtService;

    @Autowired
    private KarteService karteService;

    @Autowired
    private ChartEventService chartEventService;

    @Autowired
    private ClaimSenderService claimSenderService;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertThat(patientService).isNotNull();
        assertThat(pvtService).isNotNull();
        assertThat(karteService).isNotNull();
        assertThat(chartEventService).isNotNull();
        assertThat(claimSenderService).isNotNull();
    }

    @Test
    void servicesAreProperlyConfigured() {
        // Test that services are properly configured with dependencies
        assertThat(patientService).isInstanceOf(PatientService.class);
        assertThat(pvtService).isInstanceOf(PVTService.class);
        assertThat(karteService).isInstanceOf(KarteService.class);
        assertThat(chartEventService).isInstanceOf(ChartEventService.class);
        assertThat(claimSenderService).isInstanceOf(ClaimSenderService.class);
    }

    @Test
    void serviceMethodsAreAccessible() {
        // Test that service methods can be called without throwing exceptions
        // Note: These will use mocked data since we're not connecting to a real database

        // Test PatientService methods
        assertThat(patientService.getPatientsByName("1", "test")).isNotNull();
        assertThat(patientService.getPatientsByKana("1", "test")).isNotNull();
        assertThat(patientService.getPatientsByDigit("1", "123")).isNotNull();

        // Test PVTService methods
        assertThat(pvtService.getPvt("1", "2025-01-01", 0, null, null)).isNotNull();
        assertThat(pvtService.getPvt("1", "doctor", "unassigned", "2025-01-01", 0, null, null)).isNotNull();

        // Test KarteService methods
        assertThat(karteService.getDocumentList(1L, new java.util.Date(), false)).isNotNull();
        assertThat(karteService.getDocuments(java.util.Arrays.asList(1L, 2L, 3L))).isNotNull();

        // Test ChartEventService methods
        assertThat(chartEventService.getServerUUID()).isNotNull();
        assertThat(chartEventService.getPvtList("1")).isNotNull();

        // Test ClaimSenderService methods
        assertThat(claimSenderService.getConnectionConfig()).isNotNull();
        assertThat(claimSenderService.isClaimSendingEnabled()).isTrue();
    }

    @Test
    void applicationPropertiesAreLoaded() {
        // Test that application properties are loaded correctly
        // This indirectly tests that the application.yml is being read
        assertThat(claimSenderService.getConnectionConfig().getHost()).isEqualTo("localhost");
        assertThat(claimSenderService.getConnectionConfig().getPort()).isEqualTo(8210);
        assertThat(claimSenderService.getConnectionConfig().getEncoding()).isEqualTo("UTF-8");
    }
}
