package kg.alatoo.e_commerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.exception.CustomBadCredentialsException;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.AuthService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, UserRepository userRepository, @Lazy AuthService authService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal principal) {
            Map<String, Object> attributes = principal.getAttributes();
            String githubId = String.valueOf(attributes.get("id"));

            User user = userRepository.findByGithubId(githubId)
                    .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

            UserLoginResponse loginResponse = authService.convertToResponse(user);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), loginResponse);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }

}