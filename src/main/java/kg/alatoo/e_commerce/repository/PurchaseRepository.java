package kg.alatoo.e_commerce.repository;

import kg.alatoo.e_commerce.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
