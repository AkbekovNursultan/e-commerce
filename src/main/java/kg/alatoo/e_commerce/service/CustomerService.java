package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.dto.product.ProductResponse;
import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;

import java.util.List;

public interface CustomerService {
    CustomerInfoResponse customerInfo(String token);

    void update(String token, CustomerInfoResponse request);

    void changePassword(String token, ChangePasswordRequest request);

    void addFavorite(String token, Long productId);

    void deleteFavorite(String token, Long productId);

    List<ProductResponse> getFavorites(String token);
}
