package com.poly.controllers;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.CommentRequest;
import com.poly.models.responses.CommentResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
	
	private final CommentService commentService;
	
	@PostMapping
	public CommentResponse create(@RequestBody CommentRequest request) {
		return commentService.create(request);
	}
	
	@PutMapping("{id}")
	public CommentResponse update(@PathVariable Long id, @RequestBody CommentRequest request) {
		return commentService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Long id) {
		commentService.delete(id);
	}
	
	@GetMapping
	public PageResponse<CommentResponse> filterComments(
			@RequestParam(required = false) Integer productId,
			@RequestParam(required = false) String username,
			@RequestParam(required = false) LocalDate fromDate,
			@RequestParam(required = false) LocalDate toDate,
			@RequestParam(defaultValue = "DESC")String sortOrder,
			@RequestParam(defaultValue = "1") Integer pageNumber,
			@RequestParam(defaultValue = "5") Integer pageSize
			) {
		return commentService.filterComments(productId, username, fromDate, sortOrder, toDate, pageNumber, pageSize);
	}
}
