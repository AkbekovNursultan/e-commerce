package kg.alatoo.e_commerce.repository;

import kg.alatoo.e_commerce.entity.Customer;
import kg.alatoo.e_commerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByFavoritesListContaining(Product product);
}
