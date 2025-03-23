package org.garagesale.repository;

import org.garagesale.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.appUser.appUserId = ?1")
    List<Product> findProductsByAppUserId(Long userId);
}
