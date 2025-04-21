package com.ecommerce.electronicstore.service.impl;

import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.ProductDto;
import com.ecommerce.electronicstore.entity.Category;
import com.ecommerce.electronicstore.entity.Product;
import com.ecommerce.electronicstore.exception.ResourceNotFoundException;
import com.ecommerce.electronicstore.helper.Helper;
import com.ecommerce.electronicstore.repository.CategoryRepository;
import com.ecommerce.electronicstore.repository.ProductRepository;
import com.ecommerce.electronicstore.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
/**
 * Service implementation for managing product operations.
 *
 * Handles business logic related to product creation, update, deletion,
 * and retrieval, while communicating with the ProductRepository.
 *
 * @author Jagdish
 * @since 2025-04-21
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Value("${product.image.path}")
    private String prodImageUpload;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }
    private final Logger logger= LoggerFactory.getLogger(ProductServiceImpl.class);

    /**
     * Create a new product with a unique ID and save it to the database.
     *  @param productDto the product DTO containing input data
     *  @return the created product as DTO
     */
    @Override
    public ProductDto create(ProductDto productDto) {
        logger.info("[ProductService][create] Creating new product with title: {}", productDto.getTitle());
        String productId= UUID.randomUUID().toString();
        productDto.setProductId(productId);
        Product product = modelMapper.map(productDto, Product.class);
        product.setAddedDate(new Date());
        Product saveProduct = productRepository.save(product);
        logger.info("[ProductService][create] Product created successfully with ID: {}", productId);
        return modelMapper.map(saveProduct, ProductDto.class);
    }

    /**
     * Update product details.
     *  @param productDto the new data
     * @param productId  the ID of the product to update
     * @return updated product DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product oldProd = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("[ProductService][update] Product with ID {} not found", productId);
                    return new ResourceNotFoundException("Product with ID " + productId + " not found");
                });
        oldProd.setTitle(productDto.getTitle());
        oldProd.setDescription(productDto.getDescription());
        oldProd.setPrice(productDto.getPrice());
        oldProd.setDiscountedPrice(productDto.getDiscountedPrice());
        oldProd.setQuantity(productDto.getQuantity());
        oldProd.setLive(productDto.isLive());
        oldProd.setStock(productDto.isStock());
        oldProd.setProductImageName(productDto.getProductImageName());
        Product updatedProd=productRepository.save(oldProd);
        logger.info("[ProductService][update]product with ID: {} updated successfully.", productId);
        return modelMapper.map(updatedProd, ProductDto.class);
    }
    /**
     * Delete a product and its image file.
     * @param productId the product ID to delete
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    public void delete(String productId) {
        logger.info("[ProductService][delete] Deleting product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("[ProductService][delete] Product with ID {} not found", productId);
                    return new ResourceNotFoundException("Product with ID " + productId + " not found");
                });
        String fullImagePath = prodImageUpload + product.getProductImageName();
        Path path = Paths.get(fullImagePath);
        try {
            File file = new File(fullImagePath);
            if (file.exists()) {
                logger.info("[ProductService][delete]Attempting to delete product image: " + fullImagePath);
                boolean deleted = file.delete();
                if (!deleted) {
                    logger.warn("[ProductService][delete]Direct delete failed, retrying after GC hint...");
                    System.gc();
                    Thread.sleep(100);
                    deleted = file.delete();
                }
                if (!deleted) {
                    logger.error("Failed to delete product image file: " + fullImagePath);
                }
            } else {
                logger.info("Product image not found in folder, skipping deletion."+ fullImagePath);
            }
        } catch (InterruptedException e) {
            logger.error("Error deleting file: " + fullImagePath, e);
            logger.error("Thread interrupted while deleting product image: {}", fullImagePath, e);
            Thread.currentThread().interrupt();
        }
        productRepository.delete(product);
        logger.info("Product with ID {} deleted successfully.", productId);
    }
    /**
     * Retrieves a single product by ID.
     * @param productId the product ID
     * @return found product as DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    public ProductDto get(String productId) {
        logger.info("Fetching product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found.", productId);
                    return new ResourceNotFoundException("Product with ID " + productId + " not found");
                });

        logger.info("Product fetched successfully: {}", productId);
        return modelMapper.map(product, ProductDto.class);
    }
    /**
     * Retrieves all products in a paginated and sorted format.
     *@param pageNumber the page number to retrieve (zero-based index)
     * @param pageSize the number of items per page
     * @param sortBy the field name to sort by
     * @param sortDir the direction of sort: "asc" or "desc"
     * @return a paginated response of ProductDto
     */
    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all products | page: {}, size: {}, sortBy: {}, direction: {}", pageNumber, pageSize, sortBy, sortDir);
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    /**
     * Retrieves only 'live' products.
     * @param pageNumber the page number to retrieve (zero-based index)
     * @param pageSize the number of items per page
     * @param sortBy the field name to sort by
     * @param sortDir the direction of sort: "asc" or "desc"
     * @return a paginated response of live ProductDto
     */
    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching live products only");
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    /**
     * Searches products by partial title match.
     * @param subTitle the substring to search in the product title
     * @param pageNumber the page number to retrieve (zero-based index)
     * @param pageSize the number of items per page
     * @param sortBy the field name to sort by
     * @param sortDir the direction of sort: "asc" or "desc"
     * @return a paginated response of ProductDto matching the search criteria
     */
    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Searching products by title containing: '{}'", subTitle);
        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByTitleContaining(subTitle,pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Product with categoryID " + categoryId + " not found");
                });
        String productId= UUID.randomUUID().toString();
        productDto.setProductId(productId);
        Product product = modelMapper.map(productDto, Product.class);
        product.setAddedDate(new Date());
        product.setCategory(category);
        Product saveProduct = productRepository.save(product);
        logger.info("[ProductService][create] Product created successfully with ID: {}", productId);
        return modelMapper.map(saveProduct, ProductDto.class);

    }

    @Override
    public ProductDto updateCategory(String productId, String categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("[ProductService] Product with ID {} not found", productId);
                    return new ResourceNotFoundException("Product with ID " + productId + " not found");
                });
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("category with categoryID " + categoryId + " not found");
                });
        product.setCategory(category);
        Product saveProduct = productRepository.save(product);

        return modelMapper.map(saveProduct,ProductDto.class);
    }
}
