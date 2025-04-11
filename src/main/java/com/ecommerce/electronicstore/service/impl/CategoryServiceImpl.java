package com.ecommerce.electronicstore.service.impl;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.entity.Category;
import com.ecommerce.electronicstore.entity.User;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.repository.CategoryRepository;
import com.ecommerce.electronicstore.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService{
    //Constructor Injection
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Override
    public CategoryDto create(CategoryDto categoryDto) {

        Category category = modelMapper.map(categoryDto, Category.class);
        Category saveCategory = categoryRepository.save(category);

        return modelMapper.map(saveCategory,CategoryDto.class);
    }




    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        Category oldCat = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("For Update, the Category with ID " + categoryId + " not found"));
        oldCat.setTitle(categoryDto.getTitle());
        oldCat.setDescription(categoryDto.getDescription());
        oldCat.setCoverImage(categoryDto.getCoverImage());

        Category updatedCat=categoryRepository.save(oldCat);
        logger.info("category with ID: {} updated successfully.", categoryId);
        return modelMapper.map(updatedCat,CategoryDto.class);
    }


    @Override
    public void delete(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("For Update, the Category with ID " + categoryId + " not found"));

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll() {
        return null;
    }

    @Override
    public CategoryDto getCategoryById(String categoryId) {
        return null;
    }
}
