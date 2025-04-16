package com.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_money", nullable = false)
    private Float totalMoney;

    @Override
    public String toString() {
        return productId +":"+ quantity;
    }


}
