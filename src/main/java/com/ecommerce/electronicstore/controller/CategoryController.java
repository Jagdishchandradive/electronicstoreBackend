package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.ImageResponse;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.service.CategoryService;
import com.ecommerce.electronicstore.service.FileService;
import com.ecommerce.electronicstore.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/categories")

public class CategoryController {
    private final Logger logger= LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;
    @Value("${category.cover.image.path}")
    private String catImageUploadPath;

    @PostMapping("/create")
    public ResponseEntity<CategoryDto>createCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto categoryDto1=categoryService.create(categoryDto);
        return new ResponseEntity<>(categoryDto1, HttpStatus.CREATED);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto>updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                     @PathVariable String categoryId){
        CategoryDto updateCategory = categoryService.update(categoryDto, categoryId);
        return new ResponseEntity<>(updateCategory,HttpStatus.OK);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse>deleteCategory(@PathVariable String categoryId,@RequestBody CategoryDto categoryDto){
        categoryService.delete(categoryId);
        ApiResponse responseMessage = ApiResponse
                .builder()
                .message("Category is deleted")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<ApiResponse>(responseMessage,HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>>getAll(
            @RequestParam(value="pageNumber",defaultValue = "0",required=false)int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required=false)int pageSize,
            @RequestParam(value="sortBy",defaultValue = "title",required=false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required=false)String sortDir
    ){
        PageableResponse<CategoryDto> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse,HttpStatus.OK);
    }
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto>getSingle(@PathVariable String categoryId){
        CategoryDto categoryDto=categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }
    //upload cover image
    @PostMapping("/cover-image/{categoryId}")
    public ResponseEntity<ImageResponse>uploadCategoryCoverImage(
            @RequestParam("categoryImage") MultipartFile image,
            @PathVariable String categoryId) throws IOException {
        logger.info("Uploading cover-image for category ID: {}", categoryId);
        String coverImageName = fileService.UploadFile(image, catImageUploadPath);
        logger.info("Cover-Image uploaded successfully: {}", coverImageName);
        CategoryDto cat=categoryService.getCategoryById(categoryId);
        cat.setCoverImage(coverImageName);
        CategoryDto catDto=categoryService.update(cat,categoryId);
        ImageResponse imageResponse=ImageResponse.builder()
                .imageName(coverImageName).success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }
    //serve cover image
    @GetMapping("/cover-image/{categoryId}")
    public void serveCategoryCoverImage(@PathVariable String categoryId, HttpServletResponse response) throws IOException {
        logger.info("Serving cover-image for category ID: {}", categoryId);
        CategoryDto catById = categoryService.getCategoryById(categoryId);
        logger.info("Cover image name:{}",catById.getCoverImage());
        InputStream resource = fileService.getResource(catImageUploadPath, catById.getCoverImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());
    }
    //create product with category
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto>createProductWithCategory(
            @PathVariable("categoryId")String categoryId,
            @RequestBody ProductDto productDto){
        ProductDto productWithCategory = productService.createWithCategory(productDto, categoryId);
        return new ResponseEntity<>(productWithCategory,HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto>updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId

    ){
        ProductDto productDto = productService.updateCategory(productId, categoryId);
        return  new ResponseEntity<>(productDto,HttpStatus.OK);
    }
}
