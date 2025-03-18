package kg.alatoo.e_commerce.dto.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderHistoryResponse {
    private Long id;
    private List<PurchaseDetails> purchases;
}
