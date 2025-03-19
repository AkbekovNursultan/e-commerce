package kg.alatoo.e_commerce.dto.cart.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseDetails {
    private Long id;
    private Double cost;
    private String date;
}
