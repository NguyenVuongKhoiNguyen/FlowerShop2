package com.poly.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Product;
import com.poly.models.repositories.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository productRepo;
	
	public Product create(Product p) {
	    if (p.getId() != null && productRepo.existsById(p.getId())) {
	        throw new IllegalArgumentException("Product already exists: " + p.getId());
	    }
	    return productRepo.save(p);
	}
	
	public Product update(Integer id, Product p) {
		
		Product existing = productRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Product ID: " + id));
		
		existing.setName(p.getName());
		existing.setImage(p.getImage());
		existing.setCostPrice(p.getCostPrice());
		existing.setRetailPercentage(p.getRetailPercentage());
		existing.setAmount(p.getAmount());
		existing.setAvailable(p.getAvailable());
		existing.setCategory(p.getCategory());
		
		return productRepo.save(existing);
	}
	
	public void deleteById(Integer id) {
		if (!productRepo.existsById(id)) {
	        throw new UsernameNotFoundException("Product ID Not Found: " + id);
	    }
		productRepo.deleteById(id);
	}
	
	public Product findById(Integer id) {
		return  productRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Product ID Not Found: " + id));
	}
	
	public List<Product> findAll() {
		return productRepo.findAll();
	}
	
	public List<Product> filteredPaginated(Double minPrice, Double maxPrice, Integer categoryId, String productName, Boolean available, String sortOrder, Integer page, Integer pageSize
    ) {

        // Optional: default handling
        if (sortOrder == null) sortOrder = "DESC";
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 5;

        return productRepo.filteredPaginated(
                minPrice,
                maxPrice,
                categoryId,
                productName,
                available,
                sortOrder,
                page,
                pageSize
        );
    }
	
	public Long countFiltered(Double minPrice, Double maxPrice, Integer categoryId, String productName, Boolean available) {
		return productRepo.countFiltered(minPrice, maxPrice, categoryId, productName, available);
	}
	
	public List<Product> findTop8ByAvailableTrueOrderBySalesDESC() {
		return productRepo.findTop8ByAvailableTrueOrderBySalesDesc();
	}
	
	public List<Product> findTop8RandomAvailableProducts() {
		return productRepo.findTop8RandomAvailableProducts();
	}
}
