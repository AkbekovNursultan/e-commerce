package kg.alatoo.e_commerce.service.impl;

import kg.alatoo.e_commerce.dto.cart.CartInfoResponse;
import kg.alatoo.e_commerce.dto.cart.OrderHistoryResponse;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartElementRepository cartElementRepository;
    private CartMapper cartMapper;
    private AuthService authService;
    private OrderHistoryRepository orderHistoryRepository;
    private OrderHistoryMapper orderHistoryMapper;

    @Override
    public CartInfoResponse info(String token) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this.");
        Customer customer = user.getCustomer();

        return cartMapper.toDto(customer.getCart());
    }

    @Override
    public void buy(String token) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You aren't allowed to do this.");
        Date date = new Date();
        Cart cart = user.getCustomer().getCart();
        Purchase purchase = new Purchase();
        purchase.setPrice(cart.getPrice());
        purchase.setDate(date.toString());

        OrderHistory orderHistory = cart.getOrderHistory();
        List<Purchase> purchases = orderHistory.getPurchases();
        purchase.setOrderHistory(orderHistory);
        purchases.add(purchase);
        List<CartElement> newList = new ArrayList<>();
        cart.setPrice(0.0);
        cart.setProductsList(newList);
        orderHistoryRepository.saveAndFlush(orderHistory);
        userRepository.saveAndFlush(user);

    }

    @Override
    public OrderHistoryResponse history(String token) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You can't do this.");
        Customer customer = user.getCustomer();

        return orderHistoryMapper.toDto(customer.getCart());
    }


    @Override
    public void add(String token, Long productId, Integer quantity) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You aren't allowed to do this.");
        Customer customer = user.getCustomer();
        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty())
            throw new NotFoundException("Product with Id:" +productId+ " doesn't exist.", HttpStatus.NOT_FOUND);
        Cart cart = cartRepository.findByCustomerId(user.getCustomer().getId());

        if(cart.getPrice() == null)
            cart.setPrice(0.0);
        if(product.get().getQuantity() - quantity < 0)
            throw new BadRequestException("Not enough kolvo tovara karoche");
        if(customer.getBalance() < product.get().getPrice() * quantity)
            throw new BadRequestException("You don't have enough money on your balance. (poor bastard)");
        List<CartElement> list = cart.getProductsList();
        product.get().setQuantity(product.get().getQuantity() - quantity);
        customer.setBalance(user.getCustomer().getBalance() - product.get().getPrice() * quantity);
        cart.setPrice(cart.getPrice() + product.get().getPrice() * quantity);
        if(isElementInCart(product.get().getTitle(), list)){
            CartElement cartElement = cartElementRepository.findByCart(cart);
            cartElement.setQuantity(cartElement.getQuantity() + quantity);
            cartElement.setTotal(cartElement.getTotal() + quantity * cartElement.getPrice());
        }
        else {
            CartElement cartElement = new CartElement();
            cartElement.setQuantity(quantity);
            cartElement.setTitle(product.get().getTitle());
            cartElement.setPrice(product.get().getPrice());
            cartElement.setTotal(cartElement.getPrice() * quantity);
            cartElement.setCart(cart);
            list.add(cartElement);
            cart.setProductsList(list);

        }
        cartRepository.saveAndFlush(cart);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void delete(String token, Long elementId) {
        User user = authService.getUserFromToken(token);
        if(!user.getRole().equals(Role.CUSTOMER))
            throw new BadRequestException("You aren't allowed to do this.");
        Customer customer = user.getCustomer();
        List<CartElement> list = customer.getCart().getProductsList();
        Cart cart = customer.getCart();
        CartElement cartElement = cartElementRepository.findById(elementId).get();
        if(!isElementInCart(cartElement.getTitle(), list))
            throw new BadRequestException("Cart doesn't have this product.");
        customer.setBalance(customer.getBalance() + cartElement.getTotal());
        cart.setPrice(cart.getPrice() - cartElement.getTotal());
        list.remove(list.get(elementIndex(list, cartElement)));
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
