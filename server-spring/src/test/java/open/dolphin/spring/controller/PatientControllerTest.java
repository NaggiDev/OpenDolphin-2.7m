package open.dolphin.spring.controller;

import open.dolphin.spring.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PatientController.
 */
@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientsByName_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/name/john")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientsByKana_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/kana/ヤマダ")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientsByDigit_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/digit/1234567890")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientById_shouldReturnPatient() throws Exception {
        mockMvc.perform(get("/patient/id/12345")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientsByPvtDate_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/pvt/2025-01-01")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getTmpKartePatients_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/documents/status")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addPatient_shouldReturnPatientId() throws Exception {
        when(patientService.addPatient(any())).thenReturn(12345L);

        mockMvc.perform(post("/patient")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("12345"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updatePatient_shouldReturnUpdateCount() throws Exception {
        when(patientService.update(any())).thenReturn(1);

        mockMvc.perform(put("/patient")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientCount_shouldReturnCount() throws Exception {
        when(patientService.getPatientCount(anyString(), anyString())).thenReturn(42L);

        mockMvc.perform(get("/patient/count/12345")
                .param("fid", "1")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getAllPatients_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/all")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPatientsByCustomSearch_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/patient/custom/%5BD%5Ddiabetes")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void accessWithoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/patient/name/john")
                .param("fid", "1"))
                .andExpect(status().isUnauthorized());
    }
}
