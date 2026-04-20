package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	@Query(value = "EXEC sp_FilteredPaginatedCategories :inputStr, :sortOrder, :page, :size", nativeQuery = true)
    List<Category> filteredPaginatedCategories(
            @Param("inputStr") String inputStr,
            @Param("sortOrder") String sortOrder,
            @Param("page") int page,
            @Param("size") int size
    );
	
	@Query(value = "EXEC sp_CountFilteredCategories :inputStr", nativeQuery = true)
    Long countCategories(@Param("inputStr") String inputStr);
}
