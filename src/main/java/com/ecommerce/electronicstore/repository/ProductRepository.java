package com.ecommerce.electronicstore.repository;

import com.ecommerce.electronicstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByTitleContaining(String title, Pageable pageable);

    Page<Product> findByLiveTrue(Pageable pageable);

}
