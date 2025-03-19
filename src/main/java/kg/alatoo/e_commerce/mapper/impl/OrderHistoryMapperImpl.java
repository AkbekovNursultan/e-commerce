package kg.alatoo.e_commerce.mapper.impl;

import kg.alatoo.e_commerce.dto.cart.response.OrderHistoryResponse;
import kg.alatoo.e_commerce.dto.cart.response.PurchaseDetails;
import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.Purchase;
import kg.alatoo.e_commerce.mapper.OrderHistoryMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderHistoryMapperImpl implements OrderHistoryMapper {
    @Override
    public OrderHistoryResponse toDto(Cart cart) {
        List<PurchaseDetails> purchaseInfo = new ArrayList<>();
        for(Purchase element : cart.getOrderHistory().getPurchases()){
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setId(element.getId());
            purchaseDetails.setCost(element.getPrice());
            purchaseDetails.setDate(element.getDate());
            purchaseInfo.add(purchaseDetails);
        }
        OrderHistoryResponse response = new OrderHistoryResponse();
        response.setId(cart.getOrderHistory().getId());
        response.setPurchases(purchaseInfo);
        return response;
    }
}
