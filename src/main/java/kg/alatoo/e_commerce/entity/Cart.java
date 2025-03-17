package kg.alatoo.e_commerce.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer price;
    @OneToOne(mappedBy = "cart")
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL)
    private List<CartElement> productsList;
    @OneToOne(cascade = CascadeType.ALL)
    private OrderHistory orderHistory;
}
