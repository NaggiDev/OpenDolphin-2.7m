package open.dolphin.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for ServerInfoController.
 */
@WebMvcTest(ServerInfoController.class)
class ServerInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getServerInfo_shouldReturnServerInfo() throws Exception {
        mockMvc.perform(get("/server/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.server").value("OpenDolphin Spring Boot Server"))
                .andExpect(jsonPath("$.version").value("2.7.2-spring"))
                .andExpect(jsonPath("$.status").value("running"))
                .andExpect(jsonPath("$.framework").value("Spring Boot 3.2.0"));
    }

    @Test
    void getServerInfo_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/server/info"))
                .andExpect(status().isUnauthorized());
    }
}
