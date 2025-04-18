package com.ecommerce.electronicstore.service.impl;
import com.ecommerce.electronicstore.entity.Category;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import com.ecommerce.electronicstore.helper.Helper;
import org.modelmapper.ModelMapper;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.repository.CategoryRepository;
import com.ecommerce.electronicstore.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${category.cover.image.path}")
    private String catImagePath;

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);
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
                .orElseThrow(() -> new ResourceNotFoundException("For Delete, the Category with ID " + categoryId + " not found"));
        String fullImagePath = catImagePath + category.getCoverImage();
        Path path = Paths.get(fullImagePath);
        try {
            File file = new File(fullImagePath);
            if (file.exists()) {
                logger.info("Attempting to delete category image: " + fullImagePath);
                boolean deleted = file.delete();
                if (!deleted) {
                    logger.warn("Direct delete failed, retrying after GC hint...");
                    System.gc();
                    Thread.sleep(100);
                    deleted = file.delete();
                }
                if (!deleted) {
                    logger.error("Failed to delete category image file: " + fullImagePath);
                }
            } else {
                logger.info("Category image not found in folder, skipping deletion.");
            }
        } catch (InterruptedException e) {
            logger.error("Error deleting file: " + fullImagePath, e);
        }
        categoryRepository.delete(category);
    }


    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        return Helper.getPageableResponse(page, CategoryDto.class);
    }

    @Override
    public CategoryDto getCategoryById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("the Category with ID " + categoryId + " not found"));
        return modelMapper.map(category,CategoryDto.class);
    }
}
