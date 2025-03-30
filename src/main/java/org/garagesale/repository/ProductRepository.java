package org.garagesale.repository;

import org.garagesale.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.appUser.appUserId = ?1")
    Page<Product> findProductsByAppUserId(Long userId, Pageable pageDetails);

    @Query("SELECT p FROM Product p WHERE p.appUser.appUserId = ?1 AND p.productId = ?2")
    Optional<Product> findProductByAppUserIdAndProductId(Long userId, Long productId);
}
