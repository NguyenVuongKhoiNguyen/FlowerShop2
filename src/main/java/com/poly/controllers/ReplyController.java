package com.poly.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.ReplyRequest;
import com.poly.models.responses.ReplyResponse;
import com.poly.models.services.ReplyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
	
	private final ReplyService replyService;
	
	@PostMapping
	public ReplyResponse create(@RequestBody ReplyRequest request) {
		return replyService.create(request);
	}
	
	@PutMapping("{id}")
	public ReplyResponse update(@PathVariable Long id, @RequestBody ReplyRequest request) {
		return replyService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Long id) {
		replyService.delete(id);
	}
}
