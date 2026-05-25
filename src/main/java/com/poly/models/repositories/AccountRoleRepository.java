package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.AccountRole;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRoleId> {
	
	List<AccountRole> findByRoleId(Integer roleId);
	List<AccountRole> findByAccountUsername(String username);
	AccountRole findByAccountUsernameAndRoleId(String username, Integer roleId);
	
}
