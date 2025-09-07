package open.dolphin.spring.controller;

import open.dolphin.spring.service.UserService;
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
 * Integration tests for UserController.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getUser_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getAllUsers_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/user")
                .param("fid", "facility1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void addUser_shouldReturnSuccessCount() throws Exception {
        when(userService.addUser(any())).thenReturn(1);

        mockMvc.perform(post("/user")
                .param("fid", "facility1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateUser_shouldReturnUpdateCount() throws Exception {
        when(userService.updateUser(any())).thenReturn(1);

        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteUser_shouldReturnOk() throws Exception {
        when(userService.removeUser(anyString())).thenReturn(1);

        mockMvc.perform(delete("/user/testuser"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteUser_notFound_shouldReturnNotFound() throws Exception {
        when(userService.removeUser(anyString())).thenReturn(0);

        mockMvc.perform(delete("/user/testuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateFacility_shouldReturnUpdateCount() throws Exception {
        when(userService.updateFacility(any())).thenReturn(1);

        mockMvc.perform(put("/user/facility")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void authenticate_shouldReturnAuthenticationResult() throws Exception {
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/user/authenticate")
                .param("username", "testuser")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getCurrentUser_shouldReturnCurrentUserId() throws Exception {
        when(userService.getCurrentUserId()).thenReturn("admin");

        mockMvc.perform(get("/user/current"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getCurrentUser_noAuth_shouldReturnNotFound() throws Exception {
        when(userService.getCurrentUserId()).thenReturn(null);

        mockMvc.perform(get("/user/current"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void hasRole_shouldReturnRoleCheckResult() throws Exception {
        when(userService.hasRole(anyString())).thenReturn(true);

        mockMvc.perform(get("/user/hasRole/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // Security tests
    @Test
    void getUser_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/user/testuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/user")
                .param("fid", "facility1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addUser_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/user")
                .param("fid", "facility1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/user/testuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateFacility_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(put("/user/facility")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticate_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/user/authenticate")
                .param("username", "testuser")
                .param("password", "password"))
                .andExpect(status().isUnauthorized());
    }
}
