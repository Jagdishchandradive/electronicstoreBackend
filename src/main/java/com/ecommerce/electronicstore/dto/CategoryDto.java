package com.ecommerce.electronicstore.dto;

import com.ecommerce.electronicstore.validate.ImageNameValid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = " title cannot be blank")
    @Size(min=4,max=50,message="Title must be of 4 Characters")
    private String title;
    @NotBlank(message = "description cannot be blank")
    private String description;
    @NotBlank(message = "description cannot be empty")
    @ImageNameValid
    private String coverImage;

}
