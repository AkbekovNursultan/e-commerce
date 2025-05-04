package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        jwtService = new JwtService("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", 600000 , 1209600000);

        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_shouldContainCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void isTokenExpired_shouldReturnFalseForNewToken() {
        String token = jwtService.generateToken(userDetails);
        boolean expired = jwtService.isTokenExpired(token);
        assertFalse(expired);
    }

    @Test
    void extractClaim_shouldReturnExpirationDate() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());
        assertNotNull(expiration);
    }

    @Test
    void generateTokenWithClaims_shouldIncludeClaims() {
        String token = jwtService.generateToken(Map.of("role", "ADMIN"), userDetails);
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", role);
    }

    @Test
    void generateRefreshToken_shouldBeValid() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertNotNull(refreshToken);
        assertEquals("testuser", jwtService.extractUsername(refreshToken));
    }
}
