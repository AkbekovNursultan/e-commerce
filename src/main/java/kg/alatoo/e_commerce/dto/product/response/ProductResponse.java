package kg.alatoo.e_commerce.dto.product.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String title;
    private Double price;
    private String category;
    private Integer quantity;
}
