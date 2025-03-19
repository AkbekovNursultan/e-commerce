package kg.alatoo.e_commerce.mapper.impl;

import kg.alatoo.e_commerce.dto.cart.response.CartElementResponse;
import kg.alatoo.e_commerce.dto.cart.response.CartInfoResponse;
import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.CartElement;
import kg.alatoo.e_commerce.mapper.CartMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CartMapperImpl implements CartMapper {
    @Override
    public CartInfoResponse toDto(Cart cart) {
        List<CartElementResponse> elementsInfo = new ArrayList<>();
        for(CartElement element : cart.getProductsList()){
            CartElementResponse elementResponse = new CartElementResponse();
            elementResponse.setId(element.getId());
            elementResponse.setPrice(element.getPrice());
            elementResponse.setQuantity(element.getQuantity());
            elementResponse.setTitle(element.getTitle());
            elementResponse.setTotal(element.getTotal());
            elementsInfo.add(elementResponse);
        }
        CartInfoResponse response = new CartInfoResponse();
        response.setId(cart.getId());
        response.setList(elementsInfo);
        response.setTotalPrice(cart.getPrice());
        return response;
    }
}
