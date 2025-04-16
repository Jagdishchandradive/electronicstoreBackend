package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")

public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryDto>createCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto categoryDto1=categoryService.create(categoryDto);
        return new ResponseEntity<>(categoryDto1, HttpStatus.CREATED);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto>updateCategory(@RequestBody CategoryDto categoryDto,
                                                     @PathVariable String categoryId){
        CategoryDto updateCategory = categoryService.update(categoryDto, categoryId);
        return new ResponseEntity<>(updateCategory,HttpStatus.OK);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse>deleteCategory(@PathVariable String categoryId,@RequestBody CategoryDto categoryDto){
        categoryService.delete(categoryId);
        ApiResponse responseMessage = ApiResponse.builder().message("Category is deleted")
                .status(HttpStatus.OK).success(true).build();
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
    public ResponseEntity<PageableResponse<CategoryDto>>getAll(){

    }

}
