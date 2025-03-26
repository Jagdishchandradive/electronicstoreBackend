package com.ecommerce.electronicstore.repository;

import com.ecommerce.electronicstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {

    User findByEmail(String email);

    User findByEmailAndPassword(String email,String password);

    List<User> findByNameContaining(String keywords);

}
