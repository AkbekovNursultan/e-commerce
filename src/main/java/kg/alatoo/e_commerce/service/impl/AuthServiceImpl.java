package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.config.JwtService;
import kg.alatoo.e_commerce.dto.user.UserLoginRequest;
import kg.alatoo.e_commerce.dto.user.UserLoginResponse;
import kg.alatoo.e_commerce.dto.user.UserRegisterRequest;
import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.OrderHistory;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.entity.Worker;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.repository.CustomerRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.repository.WorkerRepository;
import kg.alatoo.e_commerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()) {
            throw new BadRequestException("User with this username already exists.");
        }

        if (!containsRole(userRegisterRequest.getRole())) {
            throw new BadRequestException("Invalid role specified.");
        }

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setRole(Role.valueOf(userRegisterRequest.getRole().toUpperCase()));

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
            workerRepository.save(worker);
        } else {
            throw new BadRequestException("Unknown role.");
        }
    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getUsername(), userLoginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found."));

        return convertToResponse(user);
    }

    @Override
    public User getUserFromToken(String token) {
        String username = jwtService.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    private UserLoginResponse convertToResponse(User user) {
        UserLoginResponse response = new UserLoginResponse();
        String jwtToken = jwtService.generateToken(new HashMap<>(), user);
        response.setToken(jwtToken);
        return response;
    }

    private boolean containsRole(String possibleRole) {
        return Arrays.stream(Role.values())
                .anyMatch(role -> role.name().equalsIgnoreCase(possibleRole));
    }
}

