package kg.alatoo.e_commerce.config;

import jakarta.servlet.http.HttpServletResponse;
import kg.alatoo.e_commerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomOAuth2UserService customOAuth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/register", "/login", "/api/login-basic", "/api/login/?error", "/api/email/**", "/h2-console/**",
                                "/oauth2/**", "/login/oauth2/**", "/api/refresh-token", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .logout(logout -> logout.permitAll())
                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler))
//                        .failureUrl("/login?error"))

        ;
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}

//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            String accept = request.getHeader("Accept");
//                            boolean isApiRequest = accept != null && accept.contains("application/json")
//                                    || request.getRequestURI().startsWith("/api");
//
//                            if (isApiRequest) {
//                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                                response.setContentType("application/json");
//                                response.getWriter().write("{\"message\": \"Token is missing or invalid.\"}");
//                            } else {
//                                response.sendRedirect("/api/login/?error");
//                            }
//                        })
//                )


//                .exceptionHandling()
//                .authenticationEntryPoint((request, response, authException) -> {
//                    String acceptHeader = request.getHeader("Accept");
//                    boolean isApiRequest = acceptHeader != null && acceptHeader.contains("application/json");
//
//                    if (isApiRequest || request.getRequestURI().startsWith("/api")) {
//                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                        response.setContentType("application/json");
//                        response.getWriter().write("{\"message\": \"Token is missing or invalid.\"}");
//                    } else {
//                        response.sendRedirect("/error");
//                    }
//                })