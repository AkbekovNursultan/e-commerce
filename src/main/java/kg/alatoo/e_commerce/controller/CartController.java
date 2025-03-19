package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.cart.response.CartInfoResponse;
import kg.alatoo.e_commerce.dto.cart.response.OrderHistoryResponse;
import kg.alatoo.e_commerce.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("/show")
    public ResponseEntity<CartInfoResponse> getCartInfo(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(cartService.info(token));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addToCart(@RequestHeader("Authorization") String token,
                                            @PathVariable Long productId,
                                            @RequestParam Integer quantity) {
        cartService.add(token, productId, quantity);
        return ResponseEntity.ok("Purchase has been made successfully!");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> removeFromCart(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long productId) {
        cartService.delete(token, productId);
        return ResponseEntity.ok("Product successfully deleted from your cart.");
    }

    @PostMapping("/buy")
    public ResponseEntity<String> purchaseCart(@RequestHeader("Authorization") String token) {
        cartService.buy(token);
        return ResponseEntity.ok("Purchase completed successfully!");
    }

    @GetMapping("/history")
    public ResponseEntity<OrderHistoryResponse> getOrderHistory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(cartService.history(token));
    }
}
