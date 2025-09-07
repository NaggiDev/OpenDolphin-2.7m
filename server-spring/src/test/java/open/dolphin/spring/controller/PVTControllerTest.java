package open.dolphin.spring.controller;

import open.dolphin.spring.service.PVTService;
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
 * Integration tests for PVTController.
 */
@WebMvcTest(PVTController.class)
class PVTControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PVTService pvtService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPvt_with4Params_shouldReturnVisits() throws Exception {
        mockMvc.perform(get("/pvt/2025-01-01,0,,1")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPvt_with6Params_shouldReturnVisits() throws Exception {
        mockMvc.perform(get("/pvt/doctor1,unassigned,2025-01-01,0,,1")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addPvt_shouldReturnVisitId() throws Exception {
        when(pvtService.addPvt(any())).thenReturn(1);

        mockMvc.perform(post("/pvt")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updatePvtState_shouldReturnUpdateCount() throws Exception {
        when(pvtService.updatePvtState(anyLong(), anyInt())).thenReturn(1);

        mockMvc.perform(put("/pvt/12345,2")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateMemo_shouldReturnUpdateCount() throws Exception {
        when(pvtService.updateMemo(anyLong(), anyString())).thenReturn(1);

        mockMvc.perform(put("/pvt/memo/12345,test memo")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateMemo_withEmptyMemo_shouldReturnUpdateCount() throws Exception {
        when(pvtService.updateMemo(anyLong(), eq(""))).thenReturn(1);

        mockMvc.perform(put("/pvt/memo/12345,")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deletePvt_shouldReturnNoContent() throws Exception {
        when(pvtService.removePvt(anyLong(), anyString())).thenReturn(1);

        mockMvc.perform(delete("/pvt/12345")
                .param("fid", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deletePvt_notFound_shouldReturnNotFound() throws Exception {
        when(pvtService.removePvt(anyLong(), anyString())).thenReturn(0);

        mockMvc.perform(delete("/pvt/12345")
                .param("fid", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPvt_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/pvt/2025-01-01,0,,1")
                .param("fid", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addPvt_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/pvt")
                .param("fid", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePvtState_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/pvt/12345,2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMemo_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/pvt/memo/12345,test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePvt_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/pvt/12345")
                .param("fid", "1"))
                .andExpect(status().isUnauthorized());
    }
}
