package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.RoleRequest;
import com.poly.models.responses.AccountResponse;

@Component
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public abstract class AccountMapper {
	
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "carts", ignore = true)
	@Mapping(target = "orders", ignore = true)
	@Mapping(target = "accountRoles", ignore = true)
	public abstract Account toEntity(AccountRequest request);
	
	@Mapping(source = "roles", target = "roleResponses") //roleMapper has to have to toResponseList(List<Role> roles)
	public abstract AccountResponse toResponse(Account account);
	
	/**
	 * in account has to have a helper method that converts accountRole list into Role list
	 * only this method is using RoleMapper under the hood
	 */
	public abstract List<AccountResponse> toResponseList(List<Account> accounts);
	
	@AfterMapping
	protected void fillAccountEmptyFields(AccountRequest request, @MappingTarget Account account) {
		
		if (request == null || request.getRoleRequests() == null || request.getRoleRequests().isEmpty()) return;
		
		for (RoleRequest rr : request.getRoleRequests()) {
			String username = account.getUsername();
			Integer roleId = rr.getId();
			AccountRoleId accountRoleId = AccountRoleId.builder().username(username).roleId(roleId).build();
			AccountRole accountRole = AccountRole.builder().id(accountRoleId).build();
			account.getAccountRoles().add(accountRole);
		}
	}
}
/**
 * source is passing value
 * target is return value
 */
