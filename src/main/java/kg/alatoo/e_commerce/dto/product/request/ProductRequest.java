package kg.alatoo.e_commerce.dto.product.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.enums.Tag;
import kg.alatoo.e_commerce.validation.ValidEnumList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Product title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Product code is required")
    private String code;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private Double price;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Colors cannot be null")
    @ValidEnumList(enumClass = Color.class, message = "Invalid color in the list")
    private List<String> colors;

    @ValidEnumList(enumClass = Tag.class, message = "Invalid tag in the list")
    private List<String> tags;

    @ValidEnumList(enumClass = ProductSize.class, message = "Invalid size in the list")
    private List<String> sizes;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}

