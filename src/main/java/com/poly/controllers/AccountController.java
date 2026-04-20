package com.poly.controllers;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.entities.Comment;
import com.poly.models.entities.Reply;
import com.poly.models.entities.Role;
import com.poly.models.mappers.AccountMapper;
import com.poly.models.mappers.CommentMapper;
import com.poly.models.mappers.ReplyMapper;
import com.poly.models.mappers.RoleMapper;
import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.CommentRequest;
import com.poly.models.requests.ReplyRequest;
import com.poly.models.requests.RoleRequest;
import com.poly.models.services.AccountService;
import com.poly.models.services.CommentService;
import com.poly.models.services.ProductService;
import com.poly.models.services.ReplyService;
import com.poly.utils.ImageUtil;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/accounts")
@RestController
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService aService;
	private final AccountMapper aMapper;
	private final CommentService coService;
	private final ReplyService reService;
	private final CommentMapper coMapper;
	private final ReplyMapper reMapper;
	private final ProductService pService;
	private final RoleMapper rMapper;
	private final PasswordEncoder password;
	
	
	@GetMapping("/{username}")
	public ResponseEntity<?> getAccountByUsername(@PathVariable String username) {
		try {
			return ResponseEntity.ok(aMapper.toResponse(aService.findById(username)));
		} catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());  // 404
		} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");      // 500
        }
	}
	
	@PostMapping("/comment")
	public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
		try {
			Comment comment = new Comment();
			comment.setAccount(aService.findById(request.getUsername()));
			comment.setProduct(pService.findById(request.getProductId()));
			comment.setContent(request.getContent());
			
			Comment saved = coService.create(comment);
			URI location = ServletUriComponentsBuilder
			            .fromCurrentRequest()
			            .path("/{id}")
			        .buildAndExpand(saved.getId())
			        .toUri();
			
			return ResponseEntity.created(location).body(coMapper.toResponse(saved));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PostMapping("/reply")
	public ResponseEntity<?> createReply(@RequestBody ReplyRequest request) {
		try {
			Reply reply = new Reply();
			reply.setAccount(aService.findById(request.getUsername()));
			reply.setComment(coService.findById(request.getCommentId()));
			reply.setContent(request.getContent());
			
			Reply saved = reService.save(reply);
			URI location = ServletUriComponentsBuilder
		            .fromCurrentRequest()
		            .path("/{id}")
		        .buildAndExpand(saved.getId())
		        .toUri();
		
			return ResponseEntity.created(location).body(reMapper.toResponse(saved));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(value = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
	        @Valid @RequestPart(value = "account") AccountRequest request,
	        @PathVariable String username,
	        @RequestPart(value = "file", required = false) MultipartFile file) {
	    try {
	        Account existing = aService.findById(username);   // single DB call
	        if (request != null && !request.getPassword().isBlank()) {
	        	existing.setPassword(password.encode(request.getPassword()));
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
	
	@DeleteMapping("/comment")
	public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
		try {
			coService.delete(commentId);
			return ResponseEntity.noContent().build(); //204
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");      // 500
        }
	}
	
	@DeleteMapping("/reply")
	public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
		try {
			reService.delete(replyId);
			return ResponseEntity.noContent().build(); //204
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ho Lee Fuk");      // 500
        }
	}
}
