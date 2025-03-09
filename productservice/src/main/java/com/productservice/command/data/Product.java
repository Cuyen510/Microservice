package com.productservice.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String id;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "price")
    private Float price;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Column(name = "description")
    private String description;

    @Column(name = "stock")
    private int stock;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
