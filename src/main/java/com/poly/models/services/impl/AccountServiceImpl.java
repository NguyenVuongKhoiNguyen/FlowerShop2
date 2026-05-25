package com.poly.models.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.entities.Role;
import com.poly.models.mappers.AccountMapper;
import com.poly.models.repositories.AccountRepository;
import com.poly.models.repositories.RoleRepository;
import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.AuthRequest;
import com.poly.models.requests.AuthResponse;
import com.poly.models.requests.TokenRequest;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.TokenResponse;
import com.poly.models.services.AccountService;
import com.poly.utils.JwtUtil;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
	
	private final GoogleAuthService googleAuthService;
	private final AccountRepository accountRepo;
	private final RoleRepository roleRepo;
	private final AccountMapper accountMapper;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	@Override
	@Transactional
	public AuthResponse login(AuthRequest authRequest) {
		// TODO Auto-generated method stub
		//Check username and password under the hood
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authRequest.getUsername(), 
				authRequest.getPassword()
			)
		);
		
		//If of then send token, username back to 
		Account account = accountRepo.findById(authRequest.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + authRequest.getUsername()));
		List<String> roles = account.getRoles().stream().map(Role::getName).collect(Collectors.toList());
		String token = jwtUtil.generateToken(authRequest.getUsername(), roles);
		return AuthResponse.builder()
				.token(token)
				.fullname(account.getFullname())
				.photo(account.getPhoto())
				.build();
	}
	
	@Override
	@Transactional
	public TokenResponse loginWithGoogle(TokenRequest tokenRequest) {
		String googleToken = tokenRequest.getToken();

        try {
            // 1. Verify Google token
            GoogleIdToken.Payload payload = googleAuthService.verify(googleToken);

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Account account;

            
            account = accountRepo.findByEmail(email)
            		.orElseThrow(() -> new EntityExistsException("Account already exist"));
            account = new Account();
            account.setUsername(email);
            account.setFullname(name);
            account.setPassword("");
            account.setEmail(email);
            account.setActivated(true);
            account.setPhoto("avatar.jpg");
            account.setAddress("");
            account.setPhone("");

            // Add role
            Role role = roleRepo.findByName("ROLE_USER")
            		.orElseThrow(() -> new EntityNotFoundException());

            AccountRole accountRole = new AccountRole();
            AccountRoleId accountRoleId = new AccountRoleId();

            accountRoleId.setUsername(account.getUsername());
            accountRoleId.setRoleId(role.getId());

            accountRole.setId(accountRoleId);
            accountRole.setRole(role);
            accountRole.setAccount(account);

            account.getAccountRoles().add(accountRole);

            account = accountRepo.save(account);

            // 4. Generate JWT
            String jwtToken = jwtUtil.generateToken(
                account.getUsername(),
                account.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())
            );

            return new TokenResponse(jwtToken);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
        }
	}
	
	@Override
	@Transactional
	@CachePut(value = "accountList", key = "#result.username")
	@CacheEvict(value = "accountPages", allEntries = true)
	public AccountResponse create(AccountRequest request) {
		// TODO Auto-generated method stub
		if (accountRepo.existsById(request.getUsername())) {
			throw new EntityExistsException("Account already exists with usernae: " + request.getUsername());
		}
		Account account = accountMapper.toEntity(request);
		Account saved = accountRepo.save(account);
		return accountMapper.toResponse(saved);
	}

	@Override
	@Transactional
	@CachePut(value = "accountList", key = "#request.username")
	@CacheEvict(value = "accountPages", allEntries = true)
	public AccountResponse update(AccountRequest request) {
		// TODO Auto-generated method stub
		if (!accountRepo.existsById(request.getUsername())) {
			throw new EntityNotFoundException("Account not found with username" + request.getUsername());
		}
		Account account = accountMapper.toEntity(request);
		Account saved = accountRepo.save(account);
		return accountMapper.toResponse(saved);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "accountList", key = "#username"), 
	        @CacheEvict(value = "accountPages", allEntries = true)
	})
	public void delete(String username) {
		// TODO Auto-generated method stub
		Account exist = accountRepo.findById(username)
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + username));
		accountRepo.delete(exist);
		
	}

	@Override
	@Cacheable(value = "accountList", key = "#username")
	public AccountResponse findById(String username) {
		// TODO Auto-generated method stub
		Account exist = accountRepo.findById(username)
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + username));
		return accountMapper.toResponse(exist);
	}

	@Override
	@Cacheable(value = "accountList", key = "#username")
	public AccountResponse findByEmail(String email) {
		// TODO Auto-generated method stub
		Account exist = accountRepo.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("Email not found"));
		return accountMapper.toResponse(exist);
	}

	@Override
	@Cacheable(value = "accountPages", key = "#username + '_' + #fullname + '_' + #email + '_' + #activated + '_' + #sortOrder + '_' + #pageNumber + '_' + #pageSize")
	public PageResponse<AccountResponse> filterAndPaginateAccounts(String username, String fullname, String email, Boolean activated,
			String sortOrder, Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		Sort sort = sortOrder.equalsIgnoreCase("ASC")
				? Sort.by("createDate").ascending()
				: Sort.by("createDate").descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Account> page = accountRepo.filterAccounts(username, fullname, email, activated, pageable);
		List<AccountResponse> responses = accountMapper.toResponseList(page.getContent());
		return new PageResponse<>(page, responses);
	}
}
