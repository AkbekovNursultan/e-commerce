package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.CustomerInfoResponse;
import kg.alatoo.e_commerce.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/info")
    public ResponseEntity<CustomerInfoResponse> customerProfile(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(customerService.customerInfo(token));
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestHeader("Authorization") String token,
                                         @RequestBody CustomerInfoResponse request) {
        customerService.update(token, request);
        return ResponseEntity.ok("Profile updated.");
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String token) {
        customerService.delete(token);
        return ResponseEntity.ok("Profile deleted successfully");
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ProductResponse>> favorites(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(customerService.getFavorites(token));
    }

    @PostMapping("/add_favorite/{productId}")
    public ResponseEntity<String> addFavorite(@RequestHeader("Authorization") String token,
                                              @PathVariable Long productId) {
        customerService.addFavorite(token, productId);
        return ResponseEntity.ok("Product added to favorites.");
    }

    @DeleteMapping("/delete_favorite/{productId}")
    public ResponseEntity<String> deleteFavorite(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long productId) {
        customerService.deleteFavorite(token, productId);
        return ResponseEntity.ok("Product removed from favorites.");
    }








    @PutMapping("/change_password")
    public String changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequest request){
        customerService.changePassword(token, request);
        return "Password successfully changed.";
    }

    //+
}
