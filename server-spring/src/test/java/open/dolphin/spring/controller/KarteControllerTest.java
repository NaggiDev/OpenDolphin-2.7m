package open.dolphin.spring.controller;

import open.dolphin.spring.service.KarteService;
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
 * Integration tests for KarteController.
 */
@WebMvcTest(KarteController.class)
class KarteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KarteService karteService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getKarteByPid_shouldReturnKarte() throws Exception {
        mockMvc.perform(get("/karte/pid/12345,2025-01-01")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getKarte_shouldReturnKarte() throws Exception {
        mockMvc.perform(get("/karte/12345,2025-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getDocumentList_shouldReturnDocInfoList() throws Exception {
        mockMvc.perform(get("/karte/docinfo/12345,2025-01-01,true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getDocuments_shouldReturnDocumentList() throws Exception {
        mockMvc.perform(get("/karte/documents/1,2,3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addDocument_shouldReturnDocumentId() throws Exception {
        when(karteService.addDocument(any())).thenReturn(12345L);

        mockMvc.perform(post("/karte/document")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("12345"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addDocumentWithPvtUpdate_shouldReturnDocumentId() throws Exception {
        when(karteService.addDocument(any())).thenReturn(12345L);

        mockMvc.perform(post("/karte/document/pvt/12345,2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("12345"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateDocumentTitle_shouldReturnUpdateCount() throws Exception {
        mockMvc.perform(put("/karte/document/12345")
                .content("New Title")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteDocument_shouldReturnDeletedDocIds() throws Exception {
        when(karteService.deleteDocument(anyLong())).thenReturn(java.util.Arrays.asList("DOC001", "DOC002"));

        mockMvc.perform(delete("/karte/document/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getModules_shouldReturnModuleList() throws Exception {
        mockMvc.perform(get("/karte/modules/12345,medication,2025-01-01,2025-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getDiagnosis_shouldReturnDiagnosisList() throws Exception {
        mockMvc.perform(get("/karte/diagnosis/12345,2025-01-01,true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getDiagnosis_withoutActiveOnly_shouldReturnDiagnosisList() throws Exception {
        mockMvc.perform(get("/karte/diagnosis/12345,2025-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addObservations_shouldReturnObservationIds() throws Exception {
        mockMvc.perform(post("/karte/observations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getObservations_shouldReturnObservationList() throws Exception {
        mockMvc.perform(get("/karte/observations/12345,Allergy,,2025-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getObservations_withoutFirstConfirmed_shouldReturnObservationList() throws Exception {
        mockMvc.perform(get("/karte/observations/12345,Allergy,")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updatePatientMemo_shouldReturnUpdateCount() throws Exception {
        mockMvc.perform(put("/karte/memo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getAllDocuments_shouldReturnDocumentList() throws Exception {
        mockMvc.perform(get("/karte/docinfo/all/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getAttachment_shouldReturnAttachment() throws Exception {
        mockMvc.perform(get("/karte/attachment/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getModulesEntitySearch_shouldReturnModuleList() throws Exception {
        mockMvc.perform(get("/karte/moduleSearch/12345,2025-01-01,2025-12-31,medication,vital")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Security tests
    @Test
    void getKarteByPid_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/karte/pid/12345,2025-01-01")
                .param("fid", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getKarte_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/karte/12345,2025-01-01"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addDocument_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/karte/document")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteDocument_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/karte/document/12345"))
                .andExpect(status().isUnauthorized());
    }
}
