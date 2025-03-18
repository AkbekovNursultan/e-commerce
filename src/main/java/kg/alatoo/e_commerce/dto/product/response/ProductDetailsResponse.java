package kg.alatoo.e_commerce.dto.product.response;

import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.enums.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDetailsResponse {
    private Long id;
    private String title;
    private Double price;
    private String category;
    private List<ProductSize> productSizes;
    private List<Tag> tags;
    private List<Color> colors;
    private String description;
    private Integer quantity;
    private String code;
}
