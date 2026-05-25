package com.poly.models.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	@Query(value = """
        SELECT Id, Name
        FROM Categories
        WHERE (:inputStr IS NULL OR Name LIKE '%' + :inputStr + '%')
        """,
        countQuery = """
        SELECT COUNT(*) FROM Categories
        WHERE (:inputStr IS NULL OR Name LIKE '%' + :inputStr + '%')
        """,
        nativeQuery = true)
    Page<Category> filterCategories(
            @Param("inputStr") String inputStr,
            Pageable pageable
    );

	@Query(value = "EXEC sp_CountFilteredCategories :inputStr", nativeQuery = true)
    Long countFilteredCategories(@Param("inputStr") String inputStr);
}
