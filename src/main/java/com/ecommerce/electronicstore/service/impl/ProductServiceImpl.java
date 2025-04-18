package com.ecommerce.electronicstore.service.impl;

import com.ecommerce.electronicstore.dto.CategoryDto;
import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
import com.ecommerce.electronicstore.entity.Category;
import com.ecommerce.electronicstore.entity.Product;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import com.ecommerce.electronicstore.helper.Helper;
import com.ecommerce.electronicstore.repository.ProductRepository;
import com.ecommerce.electronicstore.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }
    private final Logger logger= LoggerFactory.getLogger(ProductServiceImpl.class);
    @Override
    public ProductDto create(ProductDto productDto) {
        String productId= UUID.randomUUID().toString();
        productDto.setProductId(productId);
        Product product = modelMapper.map(productDto, Product.class);
        Product saveProduct = productRepository.save(product);

        return modelMapper.map(saveProduct, ProductDto.class);
    }


    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product oldProd = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("For Update, the Product with ID " + productId+ " not found"));
        oldProd.setTitle(productDto.getTitle());
        oldProd.setDescription(productDto.getDescription());
        oldProd.setPrice(productDto.getPrice());
        oldProd.setDiscountedPrice(productDto.getDiscountedPrice());
        oldProd.setQuantity(productDto.getQuantity());
        oldProd.setLive(productDto.isLive());
        oldProd.setStock(productDto.isStock());
        Product updatedProd=productRepository.save(oldProd);
        logger.info("product with ID: {} updated successfully.", productId);
        return modelMapper.map(updatedProd, ProductDto.class);
    }

    @Override
    public void delete(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("For Delete, the product with ID " + productId+ " not found"));
        productRepository.delete(product);
    }

    @Override
    public ProductDto get(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("the product with ID " + productId+ " not found"));
        return modelMapper.map(product,ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findbyLiveTrue(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findbyTitleContaining(subTitle,pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }
}
