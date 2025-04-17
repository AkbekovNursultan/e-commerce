package kg.alatoo.e_commerce.service;


import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    void sendVerificationLink(String email, String code);

    ResponseEntity<String> verifyEmail(String code);

    ResponseEntity<String> resendLink(UserLoginRequest request);

    String generateCode();
}
