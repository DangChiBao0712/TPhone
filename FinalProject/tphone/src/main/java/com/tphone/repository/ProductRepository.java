package com.tphone.repository;

import com.tphone.entity.Product;
import com.tphone.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByDeletedAtIsNull();

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByStatusAndDeletedAtIsNull(ProductStatus status);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByCategoryIdAndDeletedAtIsNull(Long categoryId);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByBrandIdAndDeletedAtIsNull(Long brandId);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByCategoryIdAndStatusAndDeletedAtIsNull(Long categoryId, ProductStatus status);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByBrandIdAndStatusAndDeletedAtIsNull(Long brandId, ProductStatus status);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(String keyword);

    @Query(
            value = """
                SELECT p FROM Product p
                LEFT JOIN FETCH p.category c
                LEFT JOIN FETCH p.brand b
                WHERE p.deletedAt IS NULL
                  AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                  AND (:categoryId IS NULL OR c.id = :categoryId)
                  AND (:brandId IS NULL OR b.id = :brandId)
                  AND (:minPrice IS NULL OR p.price >= :minPrice)
                  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                  AND p.status = com.tphone.enums.ProductStatus.ACTIVE
                """,
            countQuery = """
                SELECT COUNT(p) FROM Product p
                LEFT JOIN p.category c
                LEFT JOIN p.brand b
                WHERE p.deletedAt IS NULL
                  AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                  AND (:categoryId IS NULL OR c.id = :categoryId)
                  AND (:brandId IS NULL OR b.id = :brandId)
                  AND (:minPrice IS NULL OR p.price >= :minPrice)
                  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                  AND p.status = com.tphone.enums.ProductStatus.ACTIVE
                """
    )
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}