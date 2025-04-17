package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verify/{code}")
    private ResponseEntity<String> verify(@PathVariable String code){
        return emailService.verifyEmail(code);
    }

    @PostMapping("/resend-verification")
    private ResponseEntity<String> resendLink(@RequestBody UserLoginRequest request){
        return emailService.resendLink(request);
    }
}
