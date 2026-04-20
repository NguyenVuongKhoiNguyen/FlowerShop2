package com.poly.models.requests;

import lombok.Data;

@Data
public class ReplyRequest {
    private Long commentId;
    private String username;
    private String content;
}