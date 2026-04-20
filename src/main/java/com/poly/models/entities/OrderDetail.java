package com.poly.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="OrderDetails")
public class OrderDetail {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OrderId") 
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ProductId")
	public Product product;
    
    private Integer quantity;
    
    private Double price;
    
    private Double subtotal = 0.0;
    
    public void calculateSubtotal() {
    	subtotal = quantity * price;
    }
}
