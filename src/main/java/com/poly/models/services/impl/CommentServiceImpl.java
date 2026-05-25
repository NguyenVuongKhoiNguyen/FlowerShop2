package com.poly.models.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.models.entities.Comment;
import com.poly.models.mappers.CommentMapper;
import com.poly.models.repositories.CommentRepository;
import com.poly.models.requests.CommentRequest;
import com.poly.models.responses.CommentResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CommentService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	
	private final CommentRepository commentRepo;
	private final CommentMapper commentMapper;

	@Override
	@Transactional
	public CommentResponse create(CommentRequest request) {
		// TODO Auto-generated method stub
		Comment comment = commentMapper.toEntity(request);
		return commentMapper.toResponse(comment);
	}

	@Override
	@Transactional
	public CommentResponse update(Long id, CommentRequest request) {
		// TODO Auto-generated method stub
		if (!commentRepo.existsById(id)) {
			throw new EntityNotFoundException("Comment not found with Id: " + id);
		}
		Comment comment = commentMapper.toEntity(request);
		comment.setId(id);
		return commentMapper.toResponse(comment);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		Comment exist = commentRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Comment not found with Id: " + id));
		commentRepo.delete(exist);
	}

	@Override
	public CommentResponse findById(Long id) {
		// TODO Auto-generated method stub
		Comment exist = commentRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Comment not found with Id: " + id));
		return commentMapper.toResponse(exist);
	}

	@Override
	public PageResponse<CommentResponse> filterComments(Integer productId, String username, LocalDate fromDate,
			String sortOrder, LocalDate toDate, Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		Sort sort = sortOrder.equalsIgnoreCase("ASC")
				? Sort.by("id").ascending()
				: Sort.by("id").descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Comment> page = commentRepo.filterComments(productId, username, fromDate, toDate, pageable);
		List<CommentResponse> responses = commentMapper.toResponseList(page.getContent());
		return new PageResponse<>(page, responses);
	}
}
