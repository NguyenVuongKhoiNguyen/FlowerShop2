package com.poly.models.responses;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private Integer productId;
    private String username;
    private String content;
    private LocalDate createDate;
    private List<ReplyResponse> replyResponses;
}
