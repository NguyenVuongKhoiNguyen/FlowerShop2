package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.Comment;
import com.poly.models.responses.CommentResponse;

@Mapper(componentModel = "spring", uses = ReplyMapper.class)
public interface CommentMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "replies", source = "replies")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);
}