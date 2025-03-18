package kg.alatoo.e_commerce.repository;

import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.CartElement;
import kg.alatoo.e_commerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByCustomerId(Long id);

    List<Cart> findAllByProductsListContaining(Product product);
}
