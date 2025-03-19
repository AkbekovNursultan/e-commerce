package kg.alatoo.e_commerce.dto.cart.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderHistoryResponse {
    private Long id;
    private List<PurchaseDetails> purchases;
}
