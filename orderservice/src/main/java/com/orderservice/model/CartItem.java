package com.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {
    @Id
    @Column(name = "product_id")
    private Long productId;

    private Float price;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart")
    private Cart cart;



}
