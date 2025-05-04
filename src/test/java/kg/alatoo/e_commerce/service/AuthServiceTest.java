package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.config.JwtService;
import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.repository.CustomerRepository;
import kg.alatoo.e_commerce.repository.TokenRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterCustomerSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setRole("CUSTOMER");
        request.setPhone("1234567890");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(emailService.generateCode()).thenReturn("123456");

        authService.register(request);

        verify(userRepository).save(any(User.class));
        verify(customerRepository).save(any(Customer.class));
        verify(emailService).sendVerificationLink(eq("test@example.com"), eq("123456"));
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("existinguser");
        request.setRole("CUSTOMER");

        when(userRepository.findByUsername("existinguser"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setUsername("testuser");
        user.setIsEmailVerified(true);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserLoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
    }
}
