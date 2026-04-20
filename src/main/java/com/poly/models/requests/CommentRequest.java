package com.poly.models.requests;

import lombok.Data;

@Data
public class CommentRequest {
    private Integer productId;
    private String username;
    private String content;
}
