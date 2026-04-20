package com.poly.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Reply;
import com.poly.models.repositories.ReplyRepository;

@Service
public class ReplyService {
	
	@Autowired
	private ReplyRepository replyRepo;
	
	public Reply save(Reply r) {
		return replyRepo.save(r);
	}
	
	public void delete(Long id) {
		replyRepo.deleteById(id);
	}
	
	public Reply findById(Long id) {
		return replyRepo.findById(id).orElse(null);
	}
	
	public List<Reply> findByComment(Long id) {
		return replyRepo.findByComment_Id(id);
	}
}
