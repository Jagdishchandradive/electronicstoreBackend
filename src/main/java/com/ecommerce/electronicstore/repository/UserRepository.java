package com.ecommerce.electronicstore.repository;

import com.ecommerce.electronicstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

}
