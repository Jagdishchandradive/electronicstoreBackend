package com.ecommerce.electronicstore.dto;

import com.ecommerce.electronicstore.validate.ImageNameValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class UserDto {

    private String userId;
    @Size(min=3,max=20,message="Invalid Name")
    private String name;

//    @Email(message="Invalid Email..........")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",message = "Invalid User Email")
    @NotBlank(message = "Email required.......")
    private String email;

    @NotBlank(message = "password required........")
    private String password;

    @Size(min=4,max=6,message = "Invalid.....")
    private String gender;

    @NotBlank(message="About can not Blank.........")
    private String about;

    @ImageNameValid
    private String imageName;
}
