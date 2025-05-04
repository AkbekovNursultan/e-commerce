package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.Product;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.exception.NotFoundException;
import kg.alatoo.e_commerce.mapper.UserMapper;
import kg.alatoo.e_commerce.mapper.ProductMapper;
import kg.alatoo.e_commerce.repository.ProductRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import kg.alatoo.e_commerce.service.AuthService;
import kg.alatoo.e_commerce.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder encoder;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public CustomerInfoResponse customerInfo(String token) {
        User user = authService.getUserFromToken(token);
        if (user.getRole() != Role.CUSTOMER)
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "You can't do this.");

        return userMapper.toDto(user.getCustomer());
    }

    @Override
    public List<ProductResponse> getFavorites(String token) {
        User user = authService.getUserFromToken(token);
        if (user.getRole() != Role.CUSTOMER)
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "You can't do this.");
        Customer customer = user.getCustomer();
        return productMapper.toDtoS(customer.getFavoritesList() != null ? customer.getFavoritesList() : new ArrayList<>());
    }

    @Override
    public void delete(String token) {
        User user = authService.getUserFromToken(token);
        Customer customer = user.getCustomer();

        if (customer.getCart() != null) {
            customer.setCart(null); // Break link to cart
        }
        if (customer.getFavoritesList() != null) {
            customer.getFavoritesList().clear(); // Clear favorite products
        }

        userRepository.delete(user); // Now safe to delete
    }

    @Override
    public void addFavorite(String token, Long productId) {
        User user = authService.getUserFromToken(token);
        if (user.getRole() != Role.CUSTOMER)
            throw new BadRequestException("You can't do this ma frend.");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Invalid Product Id.", HttpStatus.NOT_FOUND));

        Customer customer = user.getCustomer();
        if (customer.getFavoritesList() == null) {
            customer.setFavoritesList(new ArrayList<>());
        }
        if (!customer.getFavoritesList().contains(product)) {
            customer.getFavoritesList().add(product);
            userRepository.save(user);
        }
    }

    @Override
    public void deleteFavorite(String token, Long productId) {
        User user = authService.getUserFromToken(token);
        if (user.getRole() != Role.CUSTOMER)
            throw new BadRequestException("You can't do this ma frend");

        Customer customer = user.getCustomer();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("This product doesn't exist!", HttpStatus.NOT_FOUND));

        if (customer.getFavoritesList() != null && customer.getFavoritesList().remove(product)) {
            userRepository.save(user);
        }
    }

    @Override
    public void update(String token, CustomerInfoResponse request) {
        User user = authService.getUserFromToken(token);
        if (user.getRole() != Role.CUSTOMER)
            throw new BadRequestException("You can't do this.");

        userRepository.findByUsername(request.getUsername())
                .filter(existingUser -> !existingUser.equals(user))
                .ifPresent(existingUser -> { throw new BadRequestException("This username is already in use!"); });

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        Customer customer = user.getCustomer();
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());
        customer.setPhone(request.getPhone());
        customer.setZipCode(request.getZipCode());
        customer.setAddress(request.getAddress());
        customer.setAdditionalInfo(request.getAdditionalInfo());

        userRepository.save(user);
    }

    @Override
    public void changePassword(String token, ChangePasswordRequest request) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this.");
        if(!encoder.matches(request.getCurrentPassword(), (user.getPassword())))
            throw new BadRequestException("Incorrect password.");
        if(request.getNewPassword().equals(request.getCurrentPassword()))
            throw new BadRequestException("This password is already in use!.");
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


}
