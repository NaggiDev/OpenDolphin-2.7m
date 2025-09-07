package open.dolphin.spring.controller;

import open.dolphin.spring.service.StampService;
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
 * Integration tests for StampController.
 */
@WebMvcTest(StampController.class)
class StampControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StampService stampService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStampTree_shouldReturnStampTreeHolder() throws Exception {
        mockMvc.perform(get("/stamp/tree/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void putTree_shouldReturnTreeId() throws Exception {
        when(stampService.putTree(any())).thenReturn(456L);

        mockMvc.perform(put("/stamp/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("456"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void syncTree_shouldReturnIdAndVersion() throws Exception {
        when(stampService.syncTree(any())).thenReturn("456,2");

        mockMvc.perform(put("/stamp/tree/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("456,2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void forceSyncTree_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/stamp/tree/forcesync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void putPublishedTree_shouldReturnVersion() throws Exception {
        when(stampService.updatePublishedTree(any())).thenReturn("3");

        mockMvc.perform(put("/stamp/published/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void cancelPublishedTree_shouldReturnVersion() throws Exception {
        when(stampService.cancelPublishedTree(any())).thenReturn("4");

        mockMvc.perform(put("/stamp/published/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPublishedTrees_shouldReturnPublishedTreeList() throws Exception {
        mockMvc.perform(get("/stamp/published/tree")
                .param("fid", "facility1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void subscribeTrees_shouldReturnSubscriptionIds() throws Exception {
        when(stampService.subscribeTrees(any())).thenReturn(java.util.Arrays.asList(1L, 2L, 3L));

        mockMvc.perform(put("/stamp/subscribed/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"list\":[]}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1,2,3"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void unsubscribeTrees_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/stamp/subscribed/tree/1,2,3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStamp_shouldReturnStamp() throws Exception {
        mockMvc.perform(get("/stamp/id/stamp123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStamp_notFound_shouldReturnNotFound() throws Exception {
        when(stampService.getStamp(anyString())).thenReturn(null);

        mockMvc.perform(get("/stamp/id/stamp123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStamps_shouldReturnStampList() throws Exception {
        mockMvc.perform(get("/stamp/list/stamp1,stamp2,stamp3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void putStamp_shouldReturnStampId() throws Exception {
        when(stampService.putStamp(any())).thenReturn("stamp456");

        mockMvc.perform(put("/stamp/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("stamp456"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void putStamps_shouldReturnStampIds() throws Exception {
        when(stampService.putStamp(anyList())).thenReturn(java.util.Arrays.asList("stamp1", "stamp2", "stamp3"));

        mockMvc.perform(put("/stamp/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"list\":[]}"))
                .andExpect(status().isOk())
                .andExpect(content().string("stamp1,stamp2,stamp3"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteStamp_shouldReturnOk() throws Exception {
        when(stampService.removeStamp(anyString())).thenReturn(1);

        mockMvc.perform(delete("/stamp/id/stamp123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteStamp_notFound_shouldReturnNotFound() throws Exception {
        when(stampService.removeStamp(anyString())).thenReturn(0);

        mockMvc.perform(delete("/stamp/id/stamp123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteStamps_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/stamp/list/stamp1,stamp2,stamp3"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStampTreeStats_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/stamp/tree/stats/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userPK").value("123"))
                .andExpect(jsonPath("$.hasPersonalTree").exists())
                .andExpect(jsonPath("$.subscribedTreeCount").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getPublishedTreeStats_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/stamp/published/stats")
                .param("fid", "facility1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.facilityId").value("facility1"))
                .andExpect(jsonPath("$.totalPublishedTrees").exists())
                .andExpect(jsonPath("$.localTrees").exists())
                .andExpect(jsonPath("$.globalTrees").exists());
    }

    // Security tests
    @Test
    void getStampTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/tree/123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void syncTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/tree/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forceSyncTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/tree/forcesync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putPublishedTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/published/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelPublishedTree_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/published/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPublishedTrees_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/published/tree")
                .param("fid", "facility1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void subscribeTrees_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/subscribed/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"list\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unsubscribeTrees_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/stamp/subscribed/tree/1,2,3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStamp_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/id/stamp123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStamps_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/list/stamp1,stamp2,stamp3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putStamp_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putStamps_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/stamp/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"list\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteStamp_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/stamp/id/stamp123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteStamps_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/stamp/list/stamp1,stamp2,stamp3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStampTreeStats_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/tree/stats/123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPublishedTreeStats_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/stamp/published/stats")
                .param("fid", "facility1"))
                .andExpect(status().isUnauthorized());
    }

    // Parameter validation tests
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStampTree_withInvalidUserPK_shouldHandleGracefully() throws Exception {
        mockMvc.perform(get("/stamp/tree/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Should handle gracefully
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void putTree_withInvalidJson_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/stamp/tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getStamps_withEmptyList_shouldHandleGracefully() throws Exception {
        mockMvc.perform(get("/stamp/list/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
