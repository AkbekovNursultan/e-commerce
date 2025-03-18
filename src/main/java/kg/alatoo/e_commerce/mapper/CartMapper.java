package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.cart.CartInfoResponse;
import kg.alatoo.e_commerce.entity.Cart;

public interface CartMapper {
    CartInfoResponse toDto(Cart cart);
}
