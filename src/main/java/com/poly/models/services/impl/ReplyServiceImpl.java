package com.poly.models.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Reply;
import com.poly.models.mappers.ReplyMapper;
import com.poly.models.repositories.ReplyRepository;
import com.poly.models.requests.ReplyRequest;
import com.poly.models.responses.ReplyResponse;
import com.poly.models.services.ReplyService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReplyServiceImpl implements ReplyService {
	
	@Autowired
	private ReplyRepository replyRepo;
	@Autowired
	private ReplyMapper replyMapper;

	@Override
	public ReplyResponse create(ReplyRequest request) {
		// TODO Auto-generated method stub
		Reply reply = replyMapper.toEntity(request);
		return replyMapper.toResponse(reply);
	}

	@Override
	public ReplyResponse update(Long id, ReplyRequest request) {
		// TODO Auto-generated method stub
		if (!replyRepo.existsById(id)) {
			throw new EntityNotFoundException("Reply not found with Id: " + id);
		}
		Reply reply = replyMapper.toEntity(request);
		reply.setId(id);
		return replyMapper.toResponse(reply);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		Reply exist = replyRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Reply not found with Id: " + id));
		replyRepo.delete(exist);
	}

	@Override
	public ReplyResponse findById(Long id) {
		// TODO Auto-generated method stub
		Reply exist = replyRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Reply not found with Id: " + id));
		return replyMapper.toResponse(exist);
	}

	@Override
	public List<ReplyResponse> findAll() {
		// TODO Auto-generated method stub
		List<Reply> replies = replyRepo.findAll();
		return replyMapper.toResponseList(replies);
	}
	
	
}
