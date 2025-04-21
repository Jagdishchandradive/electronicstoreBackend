package com.ecommerce.electronicstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="products")
public class Product {
    @Id
    @Column(length = 36, updatable = false, nullable = false)
    private String productId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length=10000)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int discountedPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private Date addedDate;
    private boolean live;
    private boolean stock;
    @Column(name = "product_image_name")
    private String productImageName;
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="category_id")
    private Category category;

}
