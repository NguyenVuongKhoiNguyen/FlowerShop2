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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Comments")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "productId")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "username")
	private Account account;
	
	private String content;
	
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(insertable = false, updatable = false)
	private LocalDate createDate;
    
    @OneToMany(mappedBy = "comment", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();
}
