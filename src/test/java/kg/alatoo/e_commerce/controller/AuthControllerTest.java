package kg.alatoo.e_commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("john_doe");
        request.setPassword("securePassword");

        UserLoginResponse response = new UserLoginResponse("access-token", "refresh-token");

        when(authService.login(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/login-basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    // ❌ Test login validation failure
    @Test
    void shouldFailLoginWhenUsernameBlank() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("");
        request.setPassword("somePassword");

        mockMvc.perform(post("/login-basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ✅ Test successful registration
    @Test
    void shouldRegisterSuccessfully() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("strongPassword");
        request.setRole("USER");
        request.setPhone("1234567890");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("User added successfully")));
    }

    // ❌ Test register validation failure
    @Test
    void shouldFailRegisterWithInvalidEmailAndShortUsername() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("a"); // too short
        request.setEmail("bad-email");
        request.setPassword("123"); // too short
        request.setRole("");
        request.setPhone("abc"); // invalid

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}

