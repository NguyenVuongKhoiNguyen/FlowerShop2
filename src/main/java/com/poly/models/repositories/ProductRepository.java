package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query(value = """
	        EXEC sp_FilteredPaginatedProducts
	            @MinPrice = :minPrice,
	            @MaxPrice = :maxPrice,
	            @CategoryId = :categoryId,
	            @ProductName = :productName,
	            @Available = :available,
	            @SortOrder = :sortOrder,
	            @Page = :page,
	            @PageSize = :pageSize
	        """, nativeQuery = true)
    List<Product> filteredPaginated(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Integer categoryId,
            @Param("productName") String productName,
            @Param("available") Boolean available,
            @Param("sortOrder") String sortOrder,
            @Param("page") Integer page,
            @Param("pageSize") Integer pageSize
    );
	@Query(value = """
	        EXEC sp_CountFilteredProducts
	            @MinPrice = :minPrice,
	            @MaxPrice = :maxPrice,
	            @CategoryId = :categoryId,
	            @ProductName = :productName,
	            @Available = :available
	        """, nativeQuery = true)
    Long countFiltered(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Integer categoryId,
            @Param("productName") String productName,
            @Param("available") Boolean available
    );
	
	List<Product> findTop8ByAvailableTrueOrderBySalesDesc();
	
	@Query(value = "SELECT TOP 8 * FROM Products WHERE Available = 1 ORDER BY NEWID()", nativeQuery = true)
	List<Product> findTop8RandomAvailableProducts();
}
