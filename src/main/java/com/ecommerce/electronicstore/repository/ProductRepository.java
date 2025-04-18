package com.ecommerce.electronicstore.repository;

import com.ecommerce.electronicstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product,String> {
    Page<Product>findbyTitleContaining(String title,Pageable pageable);
    Page<Product> findbyLiveTrue(Pageable pageable);

}
