package kg.alatoo.e_commerce.mapper;

import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.entity.Product;
import kg.alatoo.e_commerce.entity.Category;
import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.enums.Tag;
import kg.alatoo.e_commerce.mapper.impl.ProductMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ProductMapperImplTest {

    private ProductMapperImpl productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapperImpl();
    }

    @Test
    void toDtoS_ShouldMapListOfProductsToListOfProductResponse() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Product 1");
        product1.setPrice(10.0);
        product1.setQuantity(5);
        Category category1 = new Category();
        category1.setName("Category 1");
        product1.setCategory(category1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Product 2");
        product2.setPrice(20.0);
        product2.setQuantity(10);
        Category category2 = new Category();
        category2.setName("Category 2");
        product2.setCategory(category2);

        products.add(product1);
        products.add(product2);

        List<ProductResponse> productResponses = productMapper.toDtoS(products);

        assertEquals(2, productResponses.size());

        assertEquals(1L, productResponses.get(0).getId());
        assertEquals("Product 1", productResponses.get(0).getTitle());
        assertEquals(10.0, productResponses.get(0).getPrice());
        assertEquals("Category 1", productResponses.get(0).getCategory());
        assertEquals(5, productResponses.get(0).getQuantity());

        assertEquals(2L, productResponses.get(1).getId());
        assertEquals("Product 2", productResponses.get(1).getTitle());
        assertEquals(20.0, productResponses.get(1).getPrice());
        assertEquals("Category 2", productResponses.get(1).getCategory());
        assertEquals(10, productResponses.get(1).getQuantity());
    }

    @Test
    void toDto_ShouldMapProductToProductResponse() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setPrice(15.0);
        product.setQuantity(8);
        Category category = new Category();
        category.setName("Test Category");
        product.setCategory(category);

        ProductResponse productResponse = productMapper.toDto(product);

        assertEquals(1L, productResponse.getId());
        assertEquals("Test Product", productResponse.getTitle());
        assertEquals(15.0, productResponse.getPrice());
        assertEquals("Test Category", productResponse.getCategory());
        assertEquals(8, productResponse.getQuantity());
    }

    @Test
    void toDetailDto_ShouldMapProductToProductDetailsResponse() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Detailed Product");
        product.setPrice(25.0);
        product.setDescription("A detailed product description");
        product.setQuantity(12);
        product.setCode("DP123");

        Category category = new Category();
        category.setName("Detailed Category");
        product.setCategory(category);

        List<ProductSize> sizes = new ArrayList<>();
        sizes.add(ProductSize.S);
        sizes.add(ProductSize.M);
        product.setProductSizes(sizes);

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.OFFICE);
        product.setTags(tags);

        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);
        product.setColors(colors);

        ProductDetailsResponse detailsResponse = productMapper.toDetailDto(product);

        assertEquals(1L, detailsResponse.getId());
        assertEquals("Detailed Product", detailsResponse.getTitle());
        assertEquals(25.0, detailsResponse.getPrice());
        assertEquals("Detailed Category", detailsResponse.getCategory());
        assertEquals("Detailed Product",detailsResponse.getTitle());
        assertEquals(25.0, detailsResponse.getPrice());
        assertEquals(sizes, detailsResponse.getProductSizes());
        assertEquals(tags, detailsResponse.getTags());
        assertEquals(colors, detailsResponse.getColors());
        assertEquals("A detailed product description", detailsResponse.getDescription());
        assertEquals(12, detailsResponse.getQuantity());
        assertEquals("DP123", detailsResponse.getCode());
    }
}
