package com.poly.models.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query(value = """
        SELECT Id, ProductId, Username, Content, CreateDate
        FROM Comments
        WHERE
            (:productId IS NULL OR ProductId = :productId)
            AND (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
        ORDER BY Id DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM Comments
        WHERE
            (:productId IS NULL OR ProductId = :productId)
            AND (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fromDate IS NULL OR CreateDate >= :fromDate)
            AND (:toDate IS NULL OR CreateDate < DATEADD(DAY, 1, CAST(:toDate AS DATETIME)))
        """,
        nativeQuery = true)
    Page<Comment> filterComments(
            @Param("productId") Integer productId,
            @Param("username")  String username,
            @Param("fromDate")  LocalDate fromDate,
            @Param("toDate")    LocalDate toDate,
            Pageable pageable
    );
}
