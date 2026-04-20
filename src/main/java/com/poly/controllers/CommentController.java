package com.poly.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.mappers.CommentMapper;
import com.poly.models.mappers.ReplyMapper;
import com.poly.models.responses.CommentResponse;
import com.poly.models.responses.ReplyResponse;
import com.poly.models.services.CommentService;
import com.poly.models.services.ReplyService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService coService;
	private final ReplyService reService;
	private final CommentMapper commentMapper;
	private final ReplyMapper replyMapper;
	
	@GetMapping("{productId}")
	public ResponseEntity<?> getProductComment(@PathVariable Integer productId) {
		try {
			List<CommentResponse> comments = commentMapper.toResponseList(coService.findByProduct(productId));
			return ResponseEntity.ok(comments);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping("/replies/{commentId}")
	public ResponseEntity<?> getCommentReply(@PathVariable Long commentId) {
		try {
			List<ReplyResponse> replies = replyMapper.toResponseList(reService.findByComment(commentId));
			return ResponseEntity.ok(replies);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
