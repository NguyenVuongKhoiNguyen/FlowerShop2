package com.poly.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.entities.Role;
import com.poly.models.mappers.AccountMapper;
import com.poly.models.mappers.RoleMapper;
import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.RoleRequest;
import com.poly.models.services.AccountService;
import com.poly.utils.ImageUtil;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/dashboard/accounts")
@RestController
@RequiredArgsConstructor
public class DashboardAccountController {
		
	private final AccountMapper aMapper;
	private final AccountService aService;
	private final RoleMapper rMapper;
	private final PasswordEncoder password;
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
	        @Valid @RequestPart("account") AccountRequest request,
	        @RequestPart(value = "file", required = false) MultipartFile file) {
	    try {
	        Account account = aMapper.toEntity(request);
	        account.setPassword(password.encode(request.getPassword()));
	        // Handle photo
	        String photoName = "avatar.jpg";
	        if (file != null && !file.isEmpty()) {
	            photoName = ImageUtil.saveImage(file);
	        }
	        account.setPhoto(photoName);                         

	        // Build account roles
	        List<AccountRole> accountRoles = new ArrayList<>();
	        for (RoleRequest rr : request.getRoles()) {
	            Role role = rMapper.toEntity(rr);
	            AccountRole accountRole = new AccountRole();
	            AccountRoleId accountRoleId = new AccountRoleId();
	            accountRoleId.setRoleId(role.getId());
	            accountRoleId.setUsername(account.getUsername());
	            accountRole.setId(accountRoleId);
	            accountRole.setAccount(account);
	            accountRole.setRole(role);
	            accountRoles.add(accountRole);
	        }
	        account.setAccountRoles(accountRoles);                
	        System.out.println("Account"  + account);
	        Account saved = aService.create(account);

	        URI location = ServletUriComponentsBuilder
	                .fromCurrentRequest()
	                .path("/{username}")
	                .buildAndExpand(saved.getUsername())
	                .toUri();

	        return ResponseEntity.created(location).body(aMapper.toResponse(saved)); // return saved

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest()
	                             .body("Invalid data: " + e.getMessage());
	    } catch (ConstraintViolationException e) {
	        return ResponseEntity.badRequest()
                    .body(e.getConstraintViolations().stream()
                          .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                          .collect(Collectors.toList()));
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Ho Lee Fuk");
	    }
	}
	
	@PutMapping(value = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
	        @Valid @RequestPart(value = "account") AccountRequest request,
	        @PathVariable String username,
	        @RequestPart(value = "file", required = false) MultipartFile file) {
	    try {
	        Account existing = aService.findById(username);   // single DB call
	        if (request.getPassword() != null) {
	        	if (!request.getPassword().isBlank()) {
	        		existing.setPassword(password.encode(request.getPassword()));
				}
			}
	        existing.setFullname(request.getFullname());
	        existing.setEmail(request.getEmail());
	        existing.setPhone(request.getPhone());
	        existing.setAddress(request.getAddress());
	        existing.setActivated(request.getActivated());
	        // Handle photo
	        String oldPhoto = existing.getPhoto();
	        String newPhoto = oldPhoto;
	        if (file != null && !file.isEmpty()) {
	            newPhoto = ImageUtil.saveImage(file);         // save new first
	        }
	        existing.setPhoto(newPhoto);
	        existing.getAccountRoles().clear();
	        // Build account roles
	        for (RoleRequest rr : request.getRoles()) {
	            Role role = rMapper.toEntity(rr);
	            AccountRole accountRole = new AccountRole();
	            AccountRoleId accountRoleId = new AccountRoleId();
	            accountRoleId.setRoleId(role.getId());
	            accountRoleId.setUsername(username);
	            accountRole.setId(accountRoleId);             // assign the id
	            accountRole.setAccount(existing);
	            accountRole.setRole(role);
	            existing.getAccountRoles().add(accountRole);
	        }

	        Account updated = aService.update(username, existing);

	        if (file != null && !file.isEmpty()) {
	            ImageUtil.deleteImage(oldPhoto);              //delete old only after success
	        }

	        return ResponseEntity.ok(aMapper.toResponse(updated));

	    } catch (UsernameNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("Account not found: " + e.getMessage());
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest()
	                             .body("Invalid data: " + e.getMessage());
	    } catch (ConstraintViolationException e) {
	        return ResponseEntity.badRequest()
                    .body(e.getConstraintViolations().stream()
                          .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                          .collect(Collectors.toList()));
		} catch (Exception e) {
			e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Ho Lee Fuk");
	    }
	}
	
	@DeleteMapping("/{username}")
	public ResponseEntity<?> delete(@PathVariable String username) {
		try {
			ImageUtil.deleteImage(aService.findById(username).getPhoto());
			aService.deteleById(username);
			return ResponseEntity.noContent().build(); //204
		} catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());  // 404
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");      // 500
        }
	}

	@GetMapping
	public ResponseEntity<?> getAllThenFilterAndPaginate(
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String fullname,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Boolean activated,
			@RequestParam(defaultValue = "DESC") String sortOrderByCreateDate,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "5") Integer pageSize
			) {
		try {
	        List<Account> accounts = aService.filteredPaginatedAccounts(username, fullname, email, activated, sortOrderByCreateDate, page, pageSize);
	        return ResponseEntity.ok(aMapper.toResponseList(accounts));
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");
	    }
	}
	
	@GetMapping("/total-pages")
	public ResponseEntity<?> getTotalPages(
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String fullname,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Boolean activated,
			@RequestParam(defaultValue = "5") Integer pageSize
			) {
		
		try {
	        long totalRows = aService.countFilteredAccounts(username, fullname, email, activated);
	        if (pageSize <= 0) {
	            return ResponseEntity.badRequest().body("Page size must be greater than 0");
	        }
	        int totalPages = (int) Math.ceil((double) totalRows / pageSize);
	        return ResponseEntity.ok(totalPages);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");
	    }
	}
	
}
