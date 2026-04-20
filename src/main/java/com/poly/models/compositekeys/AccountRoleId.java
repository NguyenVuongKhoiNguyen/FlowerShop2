package com.poly.models.compositekeys;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class AccountRoleId implements Serializable {
	
    private static final long serialVersionUID = 1L; //add this so java doesn't complain
    
	private String username;
	private Integer roleId;
}
