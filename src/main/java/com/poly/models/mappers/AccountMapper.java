package com.poly.models.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.entities.Role;
import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.RoleRequest;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.RoleResponse;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface AccountMapper {

    @Mapping(target = "carts", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "accountRoles", source = "roles", qualifiedByName = "mapAccountRoles")
    Account toEntity(AccountRequest request);

    @Mapping(target = "roleResponses", source = "accountRoles", qualifiedByName = "mapRolesToResponses")
    AccountResponse toResponse(Account account);

    //set role to accountRole
    @Named("mapAccountRoles")
    default List<AccountRole> mapAccountRoles(List<RoleRequest> roleRequests) {
    	
    	if (roleRequests == null) return null;

        List<AccountRole> list = new ArrayList<>();

        for (RoleRequest r : roleRequests) {
            AccountRole ar = new AccountRole();

            // FIX: initialize ID
            AccountRoleId id = new AccountRoleId();
            id.setRoleId(r.getId());
            ar.setId(id);

            // FIX: set role (only id needed)
            Role role = new Role();
            role.setId(r.getId());
            ar.setRole(role);

            list.add(ar);
        }

        return list;
    }
    
    @Named("mapRolesToResponses")
    default List<RoleResponse> mapRolesToResponses(List<AccountRole> accountRoles) {
        if (accountRoles == null) return null;

        List<RoleResponse> list = new ArrayList<>();

        for (AccountRole ar : accountRoles) {
            Role role = ar.getRole();
            if (role != null) {
                RoleResponse r = new RoleResponse();
                r.setId(role.getId());
                r.setName(role.getName());
                r.setFullname(role.getFullname());
                list.add(r);
            }
        }

        return list;
    }
    
    List<AccountResponse> toResponseList(List<Account> accounts);
    
    @AfterMapping
    default void linkAccount(@MappingTarget Account account) {
    	if (account.getAccountRoles() != null) {
            for (AccountRole ar : account.getAccountRoles()) {
                ar.getId().setUsername(account.getUsername());
                ar.setAccount(account);
            }
        }
    }
}
