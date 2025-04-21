package com.ecommerce.electronicstore.dto;

import com.ecommerce.electronicstore.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProductDto {

    private String productId;
    @NotBlank(message = " title cannot be blank")
    @Size(min=4,max=50,message="Title must be of 4 Characters")
    private String title;
    @NotBlank(message = "description cannot be blank")
    private String description;
    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be greater than 0")
    private int price;
    private int discountedPrice;
    private int quantity;
    private Date addedDate;
    private boolean live;
    private boolean stock;
    @NotNull(message = "Product name is mandatory")
    private String productImageName;
    private CategoryDto category;
}
