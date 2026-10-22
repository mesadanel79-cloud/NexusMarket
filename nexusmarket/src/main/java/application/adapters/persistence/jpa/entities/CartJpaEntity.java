package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity (repository DTO): Active cart of each buyer.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "carts")
public class CartJpaEntity {

    @Id
    @Column(name = "cart_id", length = 40, nullable = false)
    private String cartId;

    @Column(name = "buyer_identification", length = 20, nullable = false, unique = true)
    private String buyerIdentification;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    @OrderBy
    private List<CartItemJpaEntity> items = new ArrayList<>();

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getBuyerIdentification() {
        return buyerIdentification;
    }

    public void setBuyerIdentification(String buyerIdentification) {
        this.buyerIdentification = buyerIdentification;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CartItemJpaEntity> getItems() {
        return items;
    }

    public void setItems(List<CartItemJpaEntity> items) {
        this.items = items;
    }
}
