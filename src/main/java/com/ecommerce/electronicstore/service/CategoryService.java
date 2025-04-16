package com.ecommerce.electronicstore.service;

import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.UserDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);
    CategoryDto update(CategoryDto categoryDto,String categoryId);
    void delete(String categoryId);

    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    CategoryDto getCategoryById(String categoryId);

}
