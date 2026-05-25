package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.poly.models.entities.Account;
import com.poly.models.entities.Comment;
import com.poly.models.entities.Product;
import com.poly.models.repositories.AccountRepository;
import com.poly.models.repositories.ProductRepository;
import com.poly.models.requests.CommentRequest;
import com.poly.models.responses.CommentResponse;

import jakarta.persistence.EntityNotFoundException;

@Mapper(componentModel = "spring", uses = {ReplyMapper.class})
public abstract class CommentMapper {
	
	@Autowired
	protected AccountRepository accountRepo;
	@Autowired
	protected ProductRepository productRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "account", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "replies", ignore = true)
	public abstract Comment toEntity(CommentRequest request);
	
	@Mapping(source = "account.username", target = "username")
	@Mapping(source = "product.id", target ="productId")
	@Mapping(source = "replies", target = "replyResponses")
	public abstract CommentResponse toResponse(Comment comment);
	
	public abstract List<CommentResponse> toResponseList(List<Comment> comments);
	
	@AfterMapping
	protected void fillCommentEmptyFields(CommentRequest request, @MappingTarget Comment comment) {
		Account account = accountRepo.findById(request.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + request.getUsername()));
		Product product = productRepo.findById(request.getProductId())
				.orElseThrow(() -> new EntityNotFoundException("Product not found with Id: " + request.getProductId()));
		comment.setAccount(account);
		comment.setProduct(product);
	}
}
