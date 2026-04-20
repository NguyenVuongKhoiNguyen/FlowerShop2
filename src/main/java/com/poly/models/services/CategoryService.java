package com.poly.models.services;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Category;
import com.poly.models.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;

    // CREATE
    public Category create(Category c) {
        if (c.getId() != null && categoryRepo.existsById(c.getId())) {
            throw new IllegalArgumentException("Category already exists: " + c.getId());
        }
        return categoryRepo.save(c);
    }

    // UPDATE
    public Category update(Integer id, Category c) {
        Category existing = categoryRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category ID not found: " + id));

        // update fields (adjust if you have more)
        existing.setName(c.getName());
        return categoryRepo.save(existing);
    }

    // DELETE
    public void deleteById(Integer id) {
        if (!categoryRepo.existsById(id)) {
            throw new UsernameNotFoundException("Category ID not found: " + id);
        }
        categoryRepo.deleteById(id);
    }

    // FIND BY ID
    public Category findById(Integer id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category ID not found: " + id));
    }

    // FIND ALL
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    // FILTER + PAGINATION
    public List<Category> filteredPaginatedCategories(
            String keyword,
            String sortOrder,
            Integer page,
            Integer size
    ) {

        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 5;
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "DESC";
        }

        return categoryRepo.filteredPaginatedCategories(
                keyword,
                sortOrder.toUpperCase(),
                page,
                size
        );
    }

    // COUNT
    public Long countCategories(String keyword) {
        return categoryRepo.countCategories(keyword);
    }
}