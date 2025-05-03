package kg.alatoo.e_commerce.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.alatoo.e_commerce.config.JwtService;
import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.Customer;

import kg.alatoo.e_commerce.entity.OrderHistory;
import kg.alatoo.e_commerce.entity.Token;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.enums.TokenType;
import kg.alatoo.e_commerce.exception.CustomBadCredentialsException;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.repository.CustomerRepository;
import kg.alatoo.e_commerce.repository.TokenRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.repository.WorkerRepository;
import kg.alatoo.e_commerce.service.AuthService;
import kg.alatoo.e_commerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()) {
            throw new BadRequestException("User with this username already exists.");
        }

        if (!containsRole(userRegisterRequest.getRole())) {
            throw new BadRequestException("Invalid role specified.");
        }

        User user = new User();
        if(userRegisterRequest.getId() != null) {
            user.setId(userRegisterRequest.getId());
        }
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setRole(Role.valueOf(userRegisterRequest.getRole().toUpperCase()));
        user.setEmail(userRegisterRequest.getEmail());
        user.setIsEmailVerified(false);

        String code = emailService.generateCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiration(Instant.now().plus(Duration.ofMinutes(1)));
        if (user.getRole() == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setCountry(userRegisterRequest.getCountry());
            customer.setCity(userRegisterRequest.getCity());
            customer.setAddress(userRegisterRequest.getAddress());
            customer.setAdditionalInfo(userRegisterRequest.getAdditionalInfo());
            customer.setPhone(userRegisterRequest.getPhone());
            customer.setZipCode(userRegisterRequest.getZipCode());
            customer.setBalance(10000);
            customer.setUser(user);
            customer.setFavoritesList(new ArrayList<>());

            Cart cart = new Cart();
            OrderHistory orderHistory = new OrderHistory();
            cart.setOrderHistory(orderHistory);
            orderHistory.setCart(cart);
            customer.setCart(cart);

            user.setCustomer(customer);
            userRepository.save(user);
            customerRepository.save(customer);
        } else if (user.getRole() == Role.WORKER) {
            Worker worker = new Worker();
            worker.setUser(user);
            user.setWorker(worker);
            userRepository.save(user);
        } else {
            throw new BadRequestException("Unknown role.");
        }
        emailService.sendVerificationLink(user.getEmail(), code);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new CustomBadCredentialsException("Invalid username or password."));
        if (!user.getIsEmailVerified()) {
            throw new AccessDeniedException("Please verify your email before accessing this resource.");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(),userLoginRequest.getPassword()));

        } catch (BadCredentialsException e){
            throw new CustomBadCredentialsException("Invalid username or password.");
        }

        return convertToResponse(user);
    }


    @Override
    public User getUserFromToken(String token){

        String[] chunks = token.substring(7).split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        JSONParser jsonParser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) jsonParser.parse(decoder.decode(chunks[1]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return userRepository.findByUsername(String.valueOf(object.get("sub"))).orElseThrow(() -> new RuntimeException("User with this token not found"));
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null){
            var user = this.userRepository.findByUsername(username).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)){
                var accessToken = jwtService.generateToken(user);
                UserLoginResponse authResponse = new UserLoginResponse();
                authResponse.setAccessToken(accessToken);
                authResponse.setRefreshToken(refreshToken);
                revokeAllUserTokens(user);
                saveToken(user, accessToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public UserLoginResponse convertToResponse(User user) {
        UserLoginResponse loginResponse = new UserLoginResponse();

        Map<String, Object> extraClaims = new HashMap<>();

        String jwtToken = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        loginResponse.setAccessToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        revokeAllUserTokens(user);
        saveToken(user, jwtToken);
        return loginResponse;
    }

    private void saveToken(User user, String jwtToken){
        Token token = new Token();
        token.setToken(jwtToken);
        token.setUser(user);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t ->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    private boolean containsRole(String role1) {
        for (Role role:Role.values()){
            if (role.name().equalsIgnoreCase(role1))
                return true;
        }
        return false;
    }

}


