package com.poly.models.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
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
@Table(name="Orders")
public class Order {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(insertable = false, updatable = false)
	private LocalDate createDate;
	
    private String fullname;
    
    private String phone;
    
    private String address;
    
    @Column(insertable = false)
	private String status;
	
    @ManyToOne
    @JoinColumn(name = "username")
    private Account account;
    
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
	
	private Double total = 0.0;
	
	public void calculateTotal() {
		total = 0.0;
		for (OrderDetail od : orderDetails) {
			total += od.getSubtotal();
		}
	}
}
