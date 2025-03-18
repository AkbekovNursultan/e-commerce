package kg.alatoo.e_commerce.repository;

import kg.alatoo.e_commerce.entity.Cart;
import kg.alatoo.e_commerce.entity.CartElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartElementRepository extends JpaRepository<CartElement, Long> {
    CartElement findByCart(Cart cart);
}
