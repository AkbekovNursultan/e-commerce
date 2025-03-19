package kg.alatoo.e_commerce.controller;

import jakarta.validation.Valid;
import kg.alatoo.e_commerce.dto.category.CategoryRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.request.ProductRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.exception.BadRequestException;
import kg.alatoo.e_commerce.exception.NotFoundException;
import kg.alatoo.e_commerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsResponse> showById(@PathVariable Long id) {
        ProductDetailsResponse response = productService.showById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> responseList() {
        List<ProductResponse> products = productService.getAll();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/add_category")
    public ResponseEntity<String> addCategory(@Valid @RequestBody CategoryRequest request) {
        productService.addNewCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category successfully added.");
    }

    @PostMapping("/add_new_product")
    public ResponseEntity<String> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        productService.addNewProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product was added.");
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @Valid @RequestBody ProductRequest productRequest) {
        productService.update(productId, productRequest);
        return ResponseEntity.ok("Product updated successfully.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (BadRequestException e) {
            throw new BadRequestException("Failed to delete product: " + e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException("Product with ID " + productId + " not found", HttpStatus.NOT_FOUND);
        }
    }

}
