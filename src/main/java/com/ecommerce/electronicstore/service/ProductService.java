package com.ecommerce.electronicstore.service;

import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
import java.util.*;
public interface ProductService {

    ProductDto create (ProductDto productDto);
    ProductDto update (ProductDto productDto,String productId);
    void delete(String productId);
    ProductDto get(String productId);
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir);

    //create product with category
    ProductDto createWithCategory(ProductDto productDto,String categoryId);

    //update category of product
    ProductDto updateCategory(String productId,String categoryId);


}
