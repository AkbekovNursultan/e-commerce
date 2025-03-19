package kg.alatoo.e_commerce.controller;
import kg.alatoo.e_commerce.controller.WorkerController;
import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.service.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(WorkerController.class)
public class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkerService workerService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid.token.here";
    }

    @Test
    @WithMockUser(roles = "WORKER")
    void testWorkerProfile() throws Exception {
        WorkerInfoResponse response = new WorkerInfoResponse();
        response.setId(1L);
        response.setUsername("testworker");
        response.setEmail("test@example.com");

        when(workerService.workerInfo(validToken)).thenReturn(response);

        mockMvc.perform(get("/worker")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testworker"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    void testUpdate() throws Exception {
        WorkerInfoResponse request = new WorkerInfoResponse();
        request.setUsername("newusername");

        WorkerInfoResponse expectedRequest = new WorkerInfoResponse();
        expectedRequest.setUsername("newusername");

        mockMvc.perform(put("/worker")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));

        verify(workerService, times(1)).update(eq(validToken), eq(expectedRequest));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    void testUpdate_UsernameAlreadyInUse() throws Exception {
        WorkerInfoResponse request = new WorkerInfoResponse();
        request.setUsername("existinguser");

        doThrow(new BadRequestException("This username already in use!"))
                .when(workerService).update(eq(validToken), any(WorkerInfoResponse.class));

        mockMvc.perform(put("/worker")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This username already in use!"));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    void testDelete() throws Exception {
        mockMvc.perform(delete("/worker")
                        .header("Authorization", validToken)
                        .with(csrf())) // Add CSRF protection
                .andExpect(status().isOk())
                .andExpect(content().string("Profile deleted successfully"));

        verify(workerService, times(1)).delete(validToken);
    }

}