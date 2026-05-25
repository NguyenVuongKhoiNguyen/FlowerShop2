package com.poly.models.services;

import java.time.LocalDate;

import com.poly.models.requests.CommentRequest;
import com.poly.models.responses.CommentResponse;
import com.poly.models.responses.PageResponse;

public interface CommentService {
	CommentResponse create(CommentRequest request);
	CommentResponse update(Long id, CommentRequest request);
	void delete(Long id);
	PageResponse<CommentResponse> filterComments(Integer productId, String username, LocalDate fromDate,
			String sortOrder, LocalDate toDate, Integer pageNumber, Integer pageSize);
	CommentResponse findById(Long id);
}
