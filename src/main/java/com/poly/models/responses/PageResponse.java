package com.poly.models.responses;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResponse<T> {
	private List<T> content;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    
    public PageResponse() {}
    
    public PageResponse(Page<?> page, List<T> content) {
        this.content       = content;
        this.currentPage   = page.getNumber();
        this.pageSize      = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages    = page.getTotalPages();
        this.first         = page.isFirst();
        this.last          = page.isLast();
    }
}
