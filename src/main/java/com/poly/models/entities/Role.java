package com.poly.models.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="Roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String fullname;
	
	@OneToMany(mappedBy = "role")
	private List<AccountRole> accountRoles = new ArrayList<>();
	
	//extract account from account role
    public List<Account> getAccounts() {
        return accountRoles.stream()
            .map(AccountRole::getAccount)
            .toList();
    }
}
