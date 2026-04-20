package com.poly.models.services;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poly.models.entities.Account;
import com.poly.models.repositories.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository accountRepo;
	
	public Account create(Account a) {
	    if (accountRepo.existsById(a.getUsername())) {
	        throw new IllegalArgumentException("Username already exists: " + a.getUsername());
	    }
	    return accountRepo.save(a);
	}
	
	public Account update(String username, Account a) {
		Account existing = accountRepo.findById(username).orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));
		
		if (a.getPassword() != null && !a.getPassword().isBlank() && !a.getPassword().equals("")) {
			existing.setPassword(a.getPassword());
		}
		existing.setFullname(a.getFullname());
	    existing.setEmail(a.getEmail());
	    existing.setPhone(a.getPhone());
	    existing.setActivated(a.getActivated());
	    existing.setPhoto(a.getPhoto());
	    existing.setAccountRoles(a.getAccountRoles());
		
	    return accountRepo.save(existing);
	}
	
	public void deteleById(String username) {
		if (!accountRepo.existsById(username)) {
	        throw new UsernameNotFoundException("User not found: " + username);
	    }
	    accountRepo.deleteById(username);
	}
	
	public Account findById(String username) {
		return accountRepo.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
	
	public List<Account> findAll() {
		return accountRepo.findAll();
	}
	
	public Account findByEmail(String email) {
		return accountRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
	}
	
	public List<Account> filteredPaginatedAccounts(
            String username,
            String fullname,
            String email,
            Boolean activated,
            String sortOrder,
            int page,
            int pageSize
    ) {
        return accountRepo.filteredPaginatedAccounts(
                username,
                fullname,
                email,
                activated,
                sortOrder,
                page,
                pageSize
        );
    }
	
	public Long countFilteredAccounts(
            String username,
            String fullname,
            String email,
            Boolean activated
    ) {
        return accountRepo.countFilteredAccounts(
                username,
                fullname,
                email,
                activated
        );
    }
}
