package com.poly.models.entities;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final Account account;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return account.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return account.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return account.getUsername();
	}
	
	 @Override
	 public boolean isEnabled() {
		 return account.getActivated();
	}
}
/*
 * UserDetail interface uses on entity with have many field that UserDetail class doesn't have.
 * UserDetail class uses for creating simple UserDetail.
 */