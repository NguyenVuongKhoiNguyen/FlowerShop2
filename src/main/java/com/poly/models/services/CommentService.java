package com.poly.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Comment;
import com.poly.models.repositories.CommentRepository;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepo;
	
	public Comment create(Comment co) {
		return commentRepo.save(co);
	}
	
	public void delete(Long id) {
		commentRepo.deleteById(id);
	}
	
	public Comment findById(Long id) {
		return commentRepo.findById(id).orElse(null);
	}
	
	public List<Comment> findByProduct(Integer id) {
		return commentRepo.findByProduct_Id(id);
	}
	
	public List<Comment> findByUsername(String username) {
		return commentRepo.findByAccount_Username(username);
	}
	
}
