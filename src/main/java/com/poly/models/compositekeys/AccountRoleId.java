package com.poly.models.compositekeys;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;
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
@Embeddable
public class AccountRoleId implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private String username;
	private Integer roleId;
}
