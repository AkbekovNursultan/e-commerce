package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.product.ProductResponse;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private UserMapper userMapper;
    private UserRepository userRepository;
    private AuthService authService;
    private PasswordEncoder encoder;
    private ProductRepository productRepository;
    private ProductMapper productMapper;

    @Override
    public CustomerInfoResponse customerInfo(String token) {
        System.out.println("kig");
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this.");
        Customer customer = user.getCustomer();
        return userMapper.toDto(customer);
    }


    @Override
    public List<ProductResponse> getFavorites(String token) {
        User user = authService.getUserFromToken(token);
        return productMapper.toDtoS(user.getCustomer().getFavoritesList());
    }

    @Override
    public void addFavorite(String token, Long productId) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this ma frend.");
        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty())
            throw new BadRequestException("Invalid Product Id.");
        List<Product> favoritesList = new ArrayList<>();
        if(!user.getCustomer().getFavoritesList().isEmpty())
            favoritesList = user.getCustomer().getFavoritesList();
        favoritesList.add(product.get());
        user.getCustomer().setFavoritesList(favoritesList);
        userRepository.save(user);
    }

    @Override
    public void deleteFavorite(String token, Long productId) {
        User user = authService.getUserFromToken(token);
        Customer customer = user.getCustomer();
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this ma frend");
        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty()|| !customer.getFavoritesList().contains(product.get()))
            throw new NotFoundException("This product doesn't exist!", HttpStatus.NOT_FOUND);
        customer.getFavoritesList().remove(product.get());
        userRepository.save(user);
    }

    @Override
    public void update(String token, CustomerInfoResponse request) {
        User user = authService.getUserFromToken(token);
        Optional<User> user1 = userRepository.findByUsername(request.getUsername());
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this.");
        if(request.getUsername() != null){
            if(user1.isEmpty() || user1.get() == user)
                user.setUsername(request.getUsername());
            else
                throw new BadRequestException("This username already in use!");
        }
        Customer customer = user.getCustomer();
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());
        customer.setPhone(request.getPhone());
//        if (request.getFirstName() != null)
//            user.setFirstName(request.getFirstName());
//        if (request.getLastName() != null)
//            user.setLastName(request.getLastName());
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
