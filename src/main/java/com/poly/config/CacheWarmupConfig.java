package com.poly.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.responses.OrderResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.AccountService;
import com.poly.models.services.ProductService;
import com.poly.models.services.CategoryService;
import com.poly.models.services.OrderService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheWarmupConfig {

    private final AccountService accountService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CacheManager cacheManager;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        System.out.println("WARMING ACCOUNT CACHE...");
        int pageSize = 5;
        
        try {
            for (int page = 0; page < 5; page++) {
                System.out.println("PAGE: " + page);
                
                PageResponse<AccountResponse> pageResponse1 = accountService.filterAndPaginateAccounts(
                    "",
                    "",
                    "",
                    true,
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("ACCOUNT[] SIZE: " + pageResponse1.getContent().size());
                
                PageResponse<ProductResponse> pageResponse2 = productService.filterAndPaginateProducts(
                    0.0, 
                    0.0, 
                    0, 
                    "", 
                    true, 
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("PRODUCT[] SIZE: " + pageResponse2.getContent().size());
                
                PageResponse<CategoryResponse> pageResponse3 = categoryService.filterAndPaginateCategories(
                    "",
                    "DESC",
                    page,
                    pageSize
                );
                System.out.println("CATEGORY[] SIZE: " + pageResponse3.getContent().size());

                PageResponse<OrderResponse> pageResponse4 = orderService.filterAndPaginateOrders(
                    "", 
                    "", 
                    null, 
                    null,
                    "DESC", 
                    page, 
                    pageSize);
                System.out.println("ORDER[] SIZE:" + pageResponse4.getContent().size());

            }

            System.out.println("CHECK CACHE");
            Cache cache = cacheManager.getCache("productPages");
            PageResponse<ProductResponse> value = cache.get("0.0_0.0_0__true_DESC_3_5", PageResponse.class);
            System.out.println(value.getContent());

            System.out.println("CACHE WARMUP COMPLETE");
        } catch (Exception e) {
            System.err.println("Cache warmup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}