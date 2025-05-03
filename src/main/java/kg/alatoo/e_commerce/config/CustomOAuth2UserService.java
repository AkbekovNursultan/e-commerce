package kg.alatoo.e_commerce.config;

import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.OrderHistory;
import kg.alatoo.e_commerce.entity.User;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.repository.CustomerRepository;
import kg.alatoo.e_commerce.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CustomOAuth2UserService(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String githubId = String.valueOf(attributes.get("id"));
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");

        User user = userRepository.findByGithubId(githubId).orElseGet(() -> {
            User newUser = new User();
            newUser.setGithubId(githubId);
            newUser.setUsername(login);
            newUser.setEmail(email != null ? email : login + "@github.com");
            newUser.setRole(Role.CUSTOMER);
            newUser.setIsEmailVerified(true);

            Customer customer = new Customer();
            customer.setCountry("Unknown");
            customer.setCity("Unknown");
            customer.setAddress("Unknown");
            customer.setAdditionalInfo("Unknown");
            customer.setPhone("Unknown");
            customer.setZipCode("Unknown");
            customer.setBalance(10000);
            customer.setUser(newUser);
            customer.setFavoritesList(new ArrayList<>());

            Cart cart = new Cart();
            OrderHistory orderHistory = new OrderHistory();
            cart.setOrderHistory(orderHistory);
            orderHistory.setCart(cart);
            customer.setCart(cart);

            newUser.setCustomer(customer);
            User savedUser = userRepository.save(newUser);
            customerRepository.save(customer);
            return savedUser;
        });

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return new DefaultOAuth2User(Collections.singletonList(authority), attributes, "id"); // "id" is the nameAttributeKey for GitHub
    }
}