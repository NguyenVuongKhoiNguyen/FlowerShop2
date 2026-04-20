package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.Reply;
import com.poly.models.responses.ReplyResponse;

@Mapper(componentModel = "spring")
public interface ReplyMapper {
	
    @Mapping(target = "username", source = "account.username")
    ReplyResponse toResponse(Reply reply);

    List<ReplyResponse> toResponseList(List<Reply> replies);
}