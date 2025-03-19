package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.cart.response.CartInfoResponse;
import kg.alatoo.e_commerce.dto.cart.response.OrderHistoryResponse;
import kg.alatoo.e_commerce.entity.*;
import kg.alatoo.e_commerce.enums.Role;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.exception.NotFoundException;
import kg.alatoo.e_commerce.mapper.CartMapper;
import kg.alatoo.e_commerce.mapper.OrderHistoryMapper;
import kg.alatoo.e_commerce.repository.*;
import kg.alatoo.e_commerce.service.AuthService;
import kg.alatoo.e_commerce.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartElementRepository cartElementRepository;
    private final CartMapper cartMapper;
    private final AuthService authService;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryMapper orderHistoryMapper;

    @Override
    public CartInfoResponse info(String token) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("Access denied. Only customers can perform this action.");

        return cartMapper.toDto(user.getCustomer().getCart());
    }

    @Override
    public void buy(String token) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("Access denied. Only customers can purchase items.");

        Cart cart = user.getCustomer().getCart();
        if (cart.getProductsList().isEmpty()) {
            throw new BadRequestException("Your cart is empty. Add items before purchasing.");
        }

        Purchase purchase = new Purchase();
        purchase.setPrice(cart.getPrice());
        purchase.setDate(new Date().toString());

        OrderHistory orderHistory = cart.getOrderHistory();
        orderHistory.getPurchases().add(purchase);

        cart.setPrice(0.0);
        cart.getProductsList().clear();

        orderHistoryRepository.saveAndFlush(orderHistory);
        userRepository.saveAndFlush(user);
    }

    @Override
    public OrderHistoryResponse history(String token) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("Access denied. Only customers can view order history.");

        return orderHistoryMapper.toDto(user.getCustomer().getCart());
    }

    @Override
    public void add(String token, Long productId, Integer quantity) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("Access denied. Only customers can add items to the cart.");

        Customer customer = user.getCustomer();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found.", HttpStatus.NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(customer.getId());
        cart.setPrice(cart.getPrice() == null ? 0.0 : cart.getPrice());

        if (product.getQuantity() < quantity)
            throw new BadRequestException("Insufficient stock for product: " + product.getTitle());

        double totalPrice = product.getPrice() * quantity;
        if (customer.getBalance() < totalPrice)
            throw new BadRequestException("Insufficient balance. Please top up your account.");

        List<CartElement> list = cart.getProductsList();
        product.setQuantity(product.getQuantity() - quantity);
        customer.setBalance(customer.getBalance() - totalPrice);
        cart.setPrice(cart.getPrice() + totalPrice);

        CartElement cartElement = list.stream()
                .filter(element -> element.getTitle().equals(product.getTitle()))
                .findFirst()
                .orElse(null);

        if (cartElement != null) {
            cartElement.setQuantity(cartElement.getQuantity() + quantity);
            cartElement.setTotal(cartElement.getTotal() + totalPrice);
        } else {
            cartElement = new CartElement();
            cartElement.setQuantity(quantity);
            cartElement.setTitle(product.getTitle());
            cartElement.setPrice(product.getPrice());
            cartElement.setTotal(totalPrice);
            cartElement.setCart(cart);
            list.add(cartElement);
        }

        cartRepository.saveAndFlush(cart);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void delete(String token, Long elementId) {
        User user = authService.getUserFromToken(token);
        if (!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("Access denied. Only customers can remove items from the cart.");

        Customer customer = user.getCustomer();
        Cart cart = customer.getCart();

        CartElement cartElement = cartElementRepository.findById(elementId)
                .orElseThrow(() -> new NotFoundException("Cart element not found.", HttpStatus.NOT_FOUND));

        if (!cart.getProductsList().contains(cartElement))
            throw new BadRequestException("This product is not in your cart.");

        customer.setBalance(customer.getBalance() + cartElement.getTotal());
        cart.setPrice(cart.getPrice() - cartElement.getTotal());

        cart.getProductsList().remove(cartElement);
        cartElementRepository.delete(cartElement);

        cartRepository.saveAndFlush(cart);
    }

    public Boolean isElementInCart(String title, List<CartElement> list){
        for(CartElement element1 : list){
            if(element1.getTitle().equals(title))
                return true;
        }
        return false;
    }
    public Integer elementIndex(List<CartElement> list, CartElement cartElement){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).equals(cartElement))
                return i;
        }
        return -1;
    }
}
