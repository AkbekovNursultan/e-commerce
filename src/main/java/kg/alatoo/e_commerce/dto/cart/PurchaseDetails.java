package kg.alatoo.e_commerce.dto.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseDetails {
    private Long id;
    private Double cost;
    private String date;
}
