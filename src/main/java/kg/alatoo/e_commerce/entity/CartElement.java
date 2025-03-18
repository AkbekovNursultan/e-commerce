package kg.alatoo.e_commerce.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CartElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer quantity;
    private Double price;
    private Double total;
    @ManyToOne(cascade = CascadeType.ALL)
    private Purchase purchase;
    @ManyToOne(cascade = CascadeType.ALL)
    private Cart cart;
}
