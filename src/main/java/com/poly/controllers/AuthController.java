package com.poly.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.poly.models.compositekeys.AccountRoleId;
import com.poly.models.entities.Account;
import com.poly.models.entities.AccountRole;
import com.poly.models.entities.Role;
import com.poly.models.requests.AuthRequest;
import com.poly.models.services.AccountService;
import com.poly.models.services.GoogleAuthService;
import com.poly.models.services.RoleService;
import com.poly.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final GoogleAuthService googleAuthService;
    
    private final AccountService aService;
    private final RoleService rService;
    
    //try catch in side try catch
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        //Extract roles from Spring Security
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        //Generate token WITH roles
        String token = jwtUtil.generateToken(request.getUsername(), roles);

        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody TokenRequest tokenRequest) {

        String googleToken = tokenRequest.getToken();

        try {
            // 1. Verify Google token
            GoogleIdToken.Payload payload = googleAuthService.verify(googleToken);

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Account account;

            try {
                // 2. Try to find account
                account = aService.findByEmail(email);
                System.out.println("Account exists, logging in...");
            } catch (UsernameNotFoundException e) {
                System.out.println("Account not found, creating new one...");

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
                Role role = rService.findByName("ROLE_USER");

                AccountRole accountRole = new AccountRole();
                AccountRoleId accountRoleId = new AccountRoleId();

                accountRoleId.setUsername(account.getUsername());
                accountRoleId.setRoleId(role.getId());

                accountRole.setId(accountRoleId);
                accountRole.setRole(role);
                accountRole.setAccount(account);

                account.getAccountRoles().add(accountRole);

                account = aService.create(account);
            }

            // 4. Generate JWT
            String jwtToken = jwtUtil.generateToken(
                account.getUsername(),
                List.of("ROLE_USER")
            );

            return ResponseEntity.ok(new AuthResponse(jwtToken));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Google login failed");
        }
    }
    
    public static class TokenRequest {
        private String token;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class AuthResponse {
        private String token;
        public AuthResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}