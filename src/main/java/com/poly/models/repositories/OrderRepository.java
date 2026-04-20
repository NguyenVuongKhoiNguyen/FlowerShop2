package com.poly.models.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	// Call sp_GetOrdersByUserAndDate
    @Query(value = "EXEC sp_GetOrdersByUserAndDate :username, :fullname, :fromDate, :toDate, :page, :pageSize", nativeQuery = true)
    List<Order> filteredAndPaginated(
            @Param("username") String username,
            @Param("fullname") String fullname,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("page") Integer page,
            @Param("pageSize") Integer pageSize
    );

    // Call sp_CountFilteredOrders
    @Query(value = "EXEC sp_CountFilteredOrders :username, :fullname, :fromDate, :toDate", nativeQuery = true)
    int countFilteredOrders(
            @Param("username") String username,
            @Param("fullname") String fullname,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
	
}
