package com.poly.models.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="Products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String image;

    private Double costPrice; 

    private Double retailPercentage = 0.0; 

    private Integer amount = 0;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(insertable = false, updatable = false)
    private LocalDate createDate;

    private Boolean available = false;
    
    private long sales =  0;
    
    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    public Double getRetailPrice() {
    	return costPrice + (costPrice * retailPercentage);
    }
}
