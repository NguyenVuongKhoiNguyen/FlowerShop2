package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query(value = """
        SELECT Id, Name, Image, CostPrice, RetailPercentage, CreateDate, Available, Amount, Sales, CategoryId
        FROM Products
        WHERE
            (:minPrice IS NULL OR :minPrice = 0.0 OR CostPrice >= :minPrice)
            AND (:maxPrice IS NULL OR :maxPrice = 0.0 OR CostPrice <= :maxPrice)
            AND (:categoryId IS NULL OR :categoryId = 0 OR CategoryId = :categoryId)
            AND (:productName IS NULL OR Name LIKE '%' + :productName + '%')
            AND (:available IS NULL OR Available = :available)
        """,
        countQuery = """
        SELECT COUNT(*) FROM Products
        WHERE
            (:minPrice IS NULL OR :minPrice = 0.0 OR CostPrice >= :minPrice)
            AND (:maxPrice IS NULL OR :maxPrice = 0.0 OR CostPrice <= :maxPrice)
            AND (:categoryId IS NULL OR :categoryId = 0 OR CategoryId = :categoryId)
            AND (:productName IS NULL OR Name LIKE '%' + :productName + '%')
            AND (:available IS NULL OR Available = :available)
        """,
        nativeQuery = true)
    Page<Product> filterProducts(
            @Param("minPrice")    Double minPrice,
            @Param("maxPrice")    Double maxPrice,
            @Param("categoryId")  Integer categoryId,
            @Param("productName") String productName,
            @Param("available")   Boolean available,
            Pageable pageable
    );
	
	List<Product> findTop8ByAvailableTrueOrderBySalesDesc();
	
	@Query(value = "SELECT TOP 8 * FROM Products WHERE Available = 1 ORDER BY NEWID()", nativeQuery = true)
	List<Product> findTop8RandomAvailableProducts();
}
