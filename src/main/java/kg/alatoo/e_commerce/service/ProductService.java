package kg.alatoo.e_commerce.service;

import kg.alatoo.e_commerce.dto.category.CategoryRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.request.ProductRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;

import java.util.List;


public interface ProductService {
    void addNewProduct(ProductRequest productRequest, String token);

    void addNewCategory(String token, CategoryRequest request);

    void update(String token, Long productId, ProductRequest productRequest);

    List<ProductResponse> getAll();

    ProductDetailsResponse showById(Long id);

    void deleteProduct(String token, Long productId);
}
