package com.poly.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.AccountService;
import com.poly.models.services.ProductService;
import com.poly.models.services.CategoryService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheWarmupConfig {
    private final AccountService accountService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        System.out.println("WARMING ACCOUNT CACHE...");
        int pageSize = 5;
        
        try {
            for (int page = 0; page < 5; page++) {
                System.out.println("PAGE: " + page);
                
                PageResponse<AccountResponse> pageResponse1 = accountService.filterAndPaginateAccounts(
                    null,
                    null,
                    null,
                    true,
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("ACCOUNT[] SIZE: " + pageResponse1.getContent().size());
                
                PageResponse<ProductResponse> pageResponse2 = productService.filterAndPaginateProducts(
                    null, 
                    null, 
                    null, 
                    null, 
                    true, 
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("PRODUCT[] SIZE: " + pageResponse2.getContent().size());
                
                PageResponse<CategoryResponse> pageResponse3 = categoryService.filterAndPaginateCategories(
                    null,
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("CATEGORY[] SIZE: " + pageResponse3.getContent().size());
            }
            System.out.println("CACHE WARMUP COMPLETE");
        } catch (Exception e) {
            System.err.println("Cache warmup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}