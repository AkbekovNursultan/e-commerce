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
    private CustomerService customerService;

    @GetMapping("/info")
    public CustomerInfoResponse customerProfile(@RequestHeader("Authorization") String token){
        return customerService.customerInfo(token);
    }

    @PutMapping("/update")
    public String update(@RequestHeader("Authorization") String token, @RequestBody CustomerInfoResponse request){
        customerService.update(token, request);
        return "Profile updated.";
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String token) {
        customerService.delete(token);
        return ResponseEntity.ok("Profile deleted successfully");
    }

    @PutMapping("/change_password")
    public String changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequest request){
        customerService.changePassword(token, request);
        return "Password successfully changed.";
    }

    @GetMapping("/favorites")
    public List<ProductResponse> favorites(@RequestHeader("Authorization") String token){
        return customerService.getFavorites(token);
    }
    @PostMapping("/add_favorite/{productId}")
    public String addFavorite(@RequestHeader("Authorization") String token, @PathVariable Long productId){
        customerService.addFavorite(token, productId);
        return "Done";
    }

    @DeleteMapping("/delete_favorite/{productId}")
    public String deleteFavorite(@RequestHeader("Authorization") String token, @PathVariable Long productId){
        customerService.deleteFavorite(token, productId);
        return "Done";
    }

    //+
}
