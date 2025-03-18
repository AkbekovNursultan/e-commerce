package kg.alatoo.e_commerce.dto.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartElementResponse {
    private Long id;
    private String title;
    private Integer quantity;
    private Double price;
    private Double total;
}
