package com.poly.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.AccountRole;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRoleId> {
	
	//naming convention
	void deleteByAccount_UsernameAndRole_Id(String username, Integer id);
	
	void deleteByAccount_Username(String username);
}
