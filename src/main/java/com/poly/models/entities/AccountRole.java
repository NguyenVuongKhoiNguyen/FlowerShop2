package com.poly.models.entities;

import com.poly.models.compositekeys.AccountRoleId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="AccountRoles")
public class AccountRole {
	
	@EmbeddedId
	private AccountRoleId id;
	
	@MapsId("username")
	@ManyToOne
	@JoinColumn(name="Username")
	private Account account;
	
	@MapsId("roleId")
	@ManyToOne
	@JoinColumn(name="RoleId")
	private Role role;
}
