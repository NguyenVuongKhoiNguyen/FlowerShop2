package com.poly.models.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	@Query(value = """
        SELECT Id, Username, Fullname, Address, Phone, CreateDate, Total, Status
        FROM Orders
        WHERE
            (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
        ORDER BY Id DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM Orders
        WHERE
            (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
        """,
        nativeQuery = true)
    Page<Order> filterOrders(
            @Param("username") String username,
            @Param("fullname") String fullname,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate,
            Pageable pageable
    );
}
