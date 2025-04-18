package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
import com.ecommerce.electronicstore.service.FileService;
import com.ecommerce.electronicstore.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final Logger logger= LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto){
        ProductDto productDto1=productService.create(productDto);
        return new ResponseEntity<>(productDto1, HttpStatus.CREATED);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto>updateProduct(@Valid @RequestBody ProductDto productDto,
                                                     @PathVariable String productId){
        ProductDto updateProduct = productService.update(productDto, productId);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse>deleteProduct(@PathVariable String productId,
                                                    @RequestBody ProductDto productDto){
        productService.delete(productId);
        ApiResponse responseMessage = ApiResponse
                .builder()
                .message("Product is deleted")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<ApiResponse>(responseMessage,HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>>getAll(
            @RequestParam(value="pageNumber",defaultValue = "0",required=false)int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required=false)int pageSize,
            @RequestParam(value="sortBy",defaultValue = "title",required=false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required=false)String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse,HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto>getSingle(@PathVariable String productId){
        ProductDto productDto=productService.get(productId);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>>getAllLive(
            @RequestParam(value="pageNumber",defaultValue = "0",required=false)int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required=false)int pageSize,
            @RequestParam(value="sortBy",defaultValue = "title",required=false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required=false)String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse,HttpStatus.OK);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>>searchProduct(
            @PathVariable String query,
            @RequestParam(value="pageNumber",defaultValue = "0",required=false)int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required=false)int pageSize,
            @RequestParam(value="sortBy",defaultValue = "title",required=false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required=false)String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.searchByTitle(query,pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse,HttpStatus.OK);
    }
}
