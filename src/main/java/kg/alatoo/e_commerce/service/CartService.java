package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.dto.cart.response.CartInfoResponse;
import kg.alatoo.e_commerce.dto.cart.response.OrderHistoryResponse;

public interface CartService {
    void add(String token, Long productId, Integer quantity);

    void delete(String token, Long productId);

    CartInfoResponse info(String token);

    void buy(String token);

    OrderHistoryResponse history(String token);
}
