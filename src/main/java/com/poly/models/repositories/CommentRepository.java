package com.poly.models.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByProduct_Id(Integer id);
	List<Comment> findByAccount_Username(String username);
}
