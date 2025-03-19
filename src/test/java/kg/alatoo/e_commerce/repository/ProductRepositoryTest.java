package kg.alatoo.e_commerce.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import kg.alatoo.e_commerce.entity.Product;
import kg.alatoo.e_commerce.entity.Category;
import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.enums.Color;
import kg.alatoo.e_commerce.enums.Tag;
import kg.alatoo.e_commerce.enums.ProductSize;
import kg.alatoo.e_commerce.entity.Customer;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveProduct() {
        Category category = new Category();
        entityManager.persistAndFlush(category);

        Cart cart = new Cart();
        entityManager.persistAndFlush(cart);

        Product product = new Product();
        product.setTitle("Test Product");
        product.setPrice(25.99);
        product.setDescription("A product for testing");
        product.setQuantity(100);
        product.setCategory(category);
        product.setCart(cart);
        product.setCode("TP123");

        List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        product.setColors(colors);

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.OFFICE);
        product.setTags(tags);

        List<ProductSize> sizes = new ArrayList<>();
        sizes.add(ProductSize.M);
        sizes.add(ProductSize.L);
        product.setProductSizes(sizes);

        Product savedProduct = productRepository.save(product);
        entityManager.persistAndFlush(savedProduct);

        assertThat(savedProduct.getId()).isNotNull();

        Product retrievedProduct = entityManager.find(Product.class, savedProduct.getId());
        assertThat(retrievedProduct.getTitle()).isEqualTo("Test Product");
        assertThat(retrievedProduct.getPrice()).isEqualTo(25.99);
        assertThat(retrievedProduct.getDescription()).isEqualTo("A product for testing");
        assertThat(retrievedProduct.getQuantity()).isEqualTo(100);
        assertThat(retrievedProduct.getCategory()).isEqualTo(category);
        assertThat(retrievedProduct.getCart()).isEqualTo(cart);
        assertThat(retrievedProduct.getCode()).isEqualTo("TP123");
        assertThat(retrievedProduct.getColors()).contains(Color.RED, Color.BLUE);
        assertThat(retrievedProduct.getTags()).contains(Tag.OFFICE);
        assertThat(retrievedProduct.getProductSizes()).contains(ProductSize.M, ProductSize.L);
    }

    @Test
    public void testFindProductById() {
        Category category = new Category();
        entityManager.persistAndFlush(category);

        Cart cart = new Cart();
        entityManager.persistAndFlush(cart);

        Product product = new Product();
        product.setTitle("Test Product");
        product.setCategory(category);
        product.setCart(cart);
        entityManager.persistAndFlush(product);

        Product foundProduct = productRepository.findById(product.getId()).orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getTitle()).isEqualTo("Test Product");
        assertThat(foundProduct.getCategory()).isEqualTo(category);
        assertThat(foundProduct.getCart()).isEqualTo(cart);
    }

    @Test
    public void testFindAllProducts() {
        Category category1 = new Category();
        entityManager.persistAndFlush(category1);

        Cart cart1 = new Cart();
        entityManager.persistAndFlush(cart1);

        Product product1 = new Product();
        product1.setTitle("Product 1");
        product1.setCategory(category1);
        product1.setCart(cart1);
        entityManager.persistAndFlush(product1);

        Category category2 = new Category();
        entityManager.persistAndFlush(category2);

        Cart cart2 = new Cart();
        entityManager.persistAndFlush(cart2);

        Product product2 = new Product();
        product2.setTitle("Product 2");
        product2.setCategory(category2);
        product2.setCart(cart2);
        entityManager.persistAndFlush(product2);

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2);
    }

    @Test
    public void testDeleteProduct() {
        Category category = new Category();
        entityManager.persistAndFlush(category);

        Cart cart = new Cart();
        entityManager.persistAndFlush(cart);

        Product product = new Product();
        product.setTitle("Test Product");
        product.setCategory(category);
        product.setCart(cart);
        entityManager.persistAndFlush(product);

        productRepository.delete(product);
        entityManager.flush();


        Product deletedProduct = entityManager.find(Product.class, product.getId());
        assertThat(deletedProduct).isNull();
    }

}