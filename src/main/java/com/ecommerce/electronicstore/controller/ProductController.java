package com.ecommerce.electronicstore.controller;

import com.ecommerce.electronicstore.dto.ApiResponse;
import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.ImageResponse;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
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
@RequestMapping("/products")
public class ProductController {
    private final Logger logger= LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;
    @Value("${product.image.path}")
    private String prodImageUpload;

    @PostMapping("/create")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto productDto){
        ProductDto productDto1=productService.create(productDto);
        return new ResponseEntity<>(productDto1, HttpStatus.CREATED);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto>update(@Valid @RequestBody ProductDto productDto,
                                                     @PathVariable String productId){
        ProductDto updateProduct = productService.update(productDto, productId);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse>delete(@PathVariable String productId){
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
    public ResponseEntity<ProductDto>getProduct(@PathVariable String productId){
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
    //upload product image
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse>uploadProductImage(
            @RequestParam("productImage") MultipartFile image,
            @PathVariable String productId) throws IOException {
        logger.info("Uploading product image for product ID: {}", productId);
        String prodImageName = fileService.UploadFile(image, prodImageUpload);
        logger.info("Product-Image uploaded successfully: {}", prodImageName);
        ProductDto prod=productService.get(productId);
        prod.setProductImageName(prodImageName);
        ProductDto prodDto=productService.update(prod,productId);
        ImageResponse imageResponse=ImageResponse.builder()
                .imageName(prodImageName).success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }
    @GetMapping("/product-image/{productId}")
    public void serveCategoryCoverImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
        logger.info("Serving product-image for product ID: {}", productId);
        ProductDto prodById = productService.get(productId);
        logger.info("product-image name:{}",prodById.getProductImageName());
        InputStream resource = fileService.getResource(prodImageUpload, prodById.getProductImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());
    }

}
