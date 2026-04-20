package com.poly.models.responses;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReplyResponse {
    private Long id;
    private String username;
    private String content;
    private LocalDate createDate;
}