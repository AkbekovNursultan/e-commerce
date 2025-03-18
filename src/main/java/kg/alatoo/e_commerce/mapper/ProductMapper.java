package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.entity.Product;

import java.util.List;

public interface ProductMapper {
    List<ProductResponse> toDtoS(List<Product> all);

    ProductDetailsResponse toDetailDto(Product product);
}
