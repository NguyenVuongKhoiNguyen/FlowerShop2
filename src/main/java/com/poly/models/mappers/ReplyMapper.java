package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Account;
import com.poly.models.entities.Comment;
import com.poly.models.entities.Reply;
import com.poly.models.repositories.AccountRepository;
import com.poly.models.repositories.CommentRepository;
import com.poly.models.requests.ReplyRequest;
import com.poly.models.responses.ReplyResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring")
public abstract class ReplyMapper {
	
	@Autowired
	protected AccountRepository accountRepo;
	@Autowired
	protected CommentRepository commentRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "account", ignore = true)
	@Mapping(target = "comment", ignore = true)
	public abstract Reply toEntity(ReplyRequest request);
	
	@Mapping(source = "account.username", target = "username")
	public abstract ReplyResponse toResponse(Reply reply);
	
	public abstract List<ReplyResponse> toResponseList(List<Reply> replies);
	
	@AfterMapping
	protected void fillReplyEmptyFields(ReplyRequest request, @MappingTarget Reply reply) {
		Account account = accountRepo.findById(request.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + request.getUsername()));
		Comment comment = commentRepo.findById(request.getCommentId())
				.orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + request.getCommentId()));
		reply.setAccount(account);
		reply.setComment(comment);
	}
}
