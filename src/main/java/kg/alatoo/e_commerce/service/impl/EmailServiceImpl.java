package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.exception.CustomBadCredentialsException;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final String link = "http://localhost:8080/email/verify/";
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public void sendVerificationLink(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("nursultan20052003@gmail.com");
            message.setSubject("Verify your account");
            message.setText("\n\n" + link + code + "\n\n" + "This is the link for verifying your account.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new CustomBadCredentialsException("Problem in '.properties'. " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> verifyEmail(String code) {
        Optional<User> user = userRepository.findByVerificationCode(code);
        if(user.isEmpty())
            throw new BadRequestException("Invalid link");
        if (user.get().getVerificationCodeExpiration().isBefore(Instant.now())){
            throw new BadRequestException("Link is expired.");
        }
        user.get().setIsEmailVerified(true);
        userRepository.save(user.get());
        return ResponseEntity.ok("Email is successfully verified.");
    }

    @Override
    public ResponseEntity<String> resendLink(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomBadCredentialsException("Invalid username or password."));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        } catch (org.springframework.security.authentication.BadCredentialsException e){
            throw new CustomBadCredentialsException("Invalid username or password.");
        }
        String newCode = generateCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiration(Instant.now().plus(Duration.ofMinutes(1)));
        userRepository.save(user);
        sendVerificationLink(user.getEmail(), newCode);
        return ResponseEntity.ok("New link was sent to email.");
    }

    public String generateCode(){
        String code = "";
        Random random = new Random();
        for(int k = 0; k < 6; k++) {
            if (random.nextInt(2) == 0)
                code += (char) (random.nextInt(26) + 65);
            else
                code += (char) (random.nextInt(10) + 48);
        }
        return code;
    }
}
