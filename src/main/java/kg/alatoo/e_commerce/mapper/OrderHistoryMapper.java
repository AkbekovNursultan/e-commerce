package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.cart.OrderHistoryResponse;
import kg.alatoo.e_commerce.entity.Cart;

public interface OrderHistoryMapper {
    OrderHistoryResponse toDto(Cart cart);
}
