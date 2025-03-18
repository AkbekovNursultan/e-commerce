package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.cart.CartInfoResponse;
import kg.alatoo.e_commerce.dto.cart.OrderHistoryResponse;
import kg.alatoo.e_commerce.service.CartService;
import lombok.AllArgsConstructor;
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
    private CartService cartService;
    @GetMapping("/show")
    public CartInfoResponse info(@RequestHeader("Authorization")String token){
        return cartService.info(token);
    }
    @PostMapping("/add/{productId}")
    public String add(@RequestHeader("Authorization")String token, @PathVariable Long productId, @RequestParam Integer quantity){
        cartService.add(token, productId, quantity);
        return "Purchase has been made successfully!";
    }

    @DeleteMapping("/delete/{productId}")
    public String delete(@RequestHeader("Authorization")String token, @PathVariable Long productId){
        cartService.delete(token, productId);
        return "Product successfully deleted from your cart";
    }
    @PostMapping("/buy")
    public String buy(@RequestHeader("Authorization") String token){
        cartService.buy(token);
        return "Done";
    }
    @GetMapping("/history")
    public OrderHistoryResponse history(@RequestHeader("Authorization") String token){
        return cartService.history(token);
    }
}
