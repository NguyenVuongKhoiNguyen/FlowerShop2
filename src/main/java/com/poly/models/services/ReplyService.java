package com.poly.models.services;

import java.util.List;

import com.poly.models.requests.ReplyRequest;
import com.poly.models.responses.ReplyResponse;

public interface ReplyService {
	ReplyResponse create(ReplyRequest request);
	ReplyResponse update(Long id, ReplyRequest request);
	void delete(Long id);
	ReplyResponse findById(Long id);
	List<ReplyResponse> findAll();
}
