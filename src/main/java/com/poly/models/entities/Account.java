package com.poly.models.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="Accounts")
public class Account {

    @Id
    @NotBlank(message = "Username is required")
    private String username;

    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(unique = true)
    private String email;

    private String photo;

    private String phone;

    private String address;

    private Boolean activated;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(insertable = false, updatable = false)
    private LocalDate createDate;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AccountRole> accountRoles = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    public List<Role> getRoles() {
        return accountRoles.stream()
                .map(AccountRole::getRole)
                .collect(Collectors.toList());
    }
}
