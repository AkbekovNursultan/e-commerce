package kg.alatoo.e_commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.e_commerce.dto.category.CategoryRequest;
import kg.alatoo.e_commerce.dto.product.request.ProductRequest;
import kg.alatoo.e_commerce.dto.product.response.ProductDetailsResponse;
import kg.alatoo.e_commerce.dto.product.response.ProductResponse;
import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.enums.Tag;
import kg.alatoo.e_commerce.exception.NotFoundException;
import kg.alatoo.e_commerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testShowById() throws Exception {
        ProductDetailsResponse response = new ProductDetailsResponse();
        response.setId(1L);
        response.setTitle("Product 1");
        response.setDescription("Description");
        response.setPrice(100.0);
        response.setCategory("Category");
        response.setProductSizes(Arrays.asList(ProductSize.M, ProductSize.L));
        response.setTags(Arrays.asList(Tag.OFFICE, Tag.CHAIR));
        response.setColors(Arrays.asList(Color.BLACK, Color.WHITE));
        response.setQuantity(10);
        response.setCode("CODE123");

        when(productService.showById(1L)).thenReturn(response);

        mockMvc.perform(get("/product/1")
                        .with(user("testUser").roles("WORKER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.category").value("Category"))
                .andExpect(jsonPath("$.productSizes[0]").value("M"))
                .andExpect(jsonPath("$.productSizes[1]").value("L"))
                .andExpect(jsonPath("$.tags[0]").value("OFFICE"))
                .andExpect(jsonPath("$.tags[1]").value("CHAIR"))
                .andExpect(jsonPath("$.colors[0]").value("BLACK"))
                .andExpect(jsonPath("$.colors[1]").value("WHITE"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.code").value("CODE123"));
    }

    @Test
    void testResponseList() throws Exception {
        ProductResponse response1 = new ProductResponse();
        response1.setId(1L);
        response1.setTitle("Product 1");
        response1.setPrice(100.0);
        response1.setCategory("furniture");
        response1.setQuantity(10);

        ProductResponse response2 = new ProductResponse();
        response2.setId(2L);
        response2.setTitle("Product 2");
        response2.setPrice(200.0);
        response2.setCategory("furniture");
        response2.setQuantity(20);

        List<ProductResponse> products = Arrays.asList(response1, response2);
        when(productService.getAll()).thenReturn(products);

        mockMvc.perform(get("/product/all")
                        .with(user("testUser").roles("WORKER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Product 1"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].category").value("furniture"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Product 2"))
                .andExpect(jsonPath("$[1].price").value(200.0))
                .andExpect(jsonPath("$[1].category").value("furniture"))
                .andExpect(jsonPath("$[1].quantity").value(20));
    }

    @Test
    void testAddCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("furniture");
        mockMvc.perform(post("/product/add_category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("testUser").roles("WORKER"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Category successfully added."));
    }

    @Test
    void testAddProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setTitle("Laptop");
        request.setDescription("Powerful laptop");
        request.setPrice(1200.0);
        request.setCategory("furniture");
        request.setQuantity(10);
        request.setCode("LP123");
        request.setColors(Arrays.asList("BLACK", "WHITE"));
        request.setTags(Arrays.asList("OFFICE"));
        request.setSizes(Arrays.asList("M", "L"));

        mockMvc.perform(post("/product/add_new_product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("testUser").roles("WORKER"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product was added."));
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setTitle("Updated Laptop");
        request.setDescription("Better laptop");
        request.setPrice(1500.0);
        request.setCategory("Electronics");
        request.setQuantity(5);
        request.setCode("ULP456");
        request.setColors(Arrays.asList("RED", "BLUE"));
        request.setTags(Arrays.asList("SOFA"));
        request.setSizes(Arrays.asList("S", "XL"));

        mockMvc.perform(put("/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("testUser").roles("WORKER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully."));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/product/1")
                        .with(user("testUser").roles("WORKER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        doThrow(new NotFoundException("Product not found", HttpStatus.NOT_FOUND)).when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/product/1")
                        .with(user("testUser").roles("WORKER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with ID 1 not found"));
    }
}