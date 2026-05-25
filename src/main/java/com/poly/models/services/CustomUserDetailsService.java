package com.poly.models.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Account;
import com.poly.models.repositories.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final AccountRepository accountRepo;
	
	/**
	 * Spring security will use loadUserByUsername to check username
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException  {
		// TODO Auto-generated method stub
		
		Account account = accountRepo.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("Account not found with username: " + username));
		
		List<SimpleGrantedAuthority> authorities = account.getRoles()
				.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
		
		return User.builder()
				.username(account.getUsername())
				.password(account.getPassword())
				.authorities(authorities)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.disabled(account.getActivated())
				.build();
	}
	
	
	/**
	 * After checking username
	 * It then checks password using passwordEncoder.match(request.getPassword, userDetails.getPassword())
	 * As the matter of fact it check your raw password sent from client with
	 * the encoded password inside UserDetails
	 */
}
