package com.poly.models.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.models.entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	@Query(value = """
	        SELECT Id, Username, Fullname, CreateDate, Total
	        FROM Carts
	        WHERE
	            (:username IS NULL OR Username LIKE '%' + :username + '%')
	            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
	            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
	            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
	        """,
	        countQuery = """
	        SELECT COUNT(*) FROM Carts
	        WHERE
	            (:username IS NULL OR Username LIKE '%' + :username + '%')
	            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
	            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
	            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
	        """,
	        nativeQuery = true)
	    Page<Cart> filterCarts(
	            @Param("username") String username,
	            @Param("fullname") String fullname,
	            @Param("fromDate") LocalDate fromDate,
	            @Param("toDate")   LocalDate toDate,
	            Pageable pageable
	    );
}
