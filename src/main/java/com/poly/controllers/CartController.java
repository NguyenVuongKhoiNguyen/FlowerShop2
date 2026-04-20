package com.poly.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.poly.models.entities.Account;
import com.poly.models.entities.Cart;
import com.poly.models.entities.Item;
import com.poly.models.entities.Product;
import com.poly.models.mappers.CartMapper;
import com.poly.models.requests.CartRequest;
import com.poly.models.requests.ItemRequest;
import com.poly.models.services.AccountService;
import com.poly.models.services.CartService;
import com.poly.models.services.ProductService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final ProductService pService;
    private final CartService caService;
    private final AccountService aService;
    private final CartMapper caMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CartRequest request) {
        try {
            Cart cart = caMapper.toEntity(request);

            Account account = aService.findById(request.getUsername());
            cart.setAccount(account);

            List<Item> items = new ArrayList<>();
            for (ItemRequest reqItem : request.getItems()) {
                Product product = pService.findById(reqItem.getProductId());
                Item item = new Item();
                item.setProduct(product);
                item.setQuantity(reqItem.getQuantity());
                item.calculateSubtotal();
                item.setCart(cart);
                items.add(item);
            }
            cart.setItems(items);
            cart.calculateTotal();

            Cart saved = caService.create(cart);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(caMapper.toResponse(saved)); // 201

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User not found: " + e.getMessage()); // 404
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Product not found: " + e.getMessage()); // 404
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("Invalid data: " + e.getMessage()); // 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody CartRequest request, @PathVariable Long id) {
        try {
            Cart cart = caMapper.toEntity(request);
            cart.setId(id);

            Account account = aService.findById(request.getUsername());
            cart.setAccount(account);

            List<Item> items = new ArrayList<>();
            for (ItemRequest reqItem : request.getItems()) {
                Product product = pService.findById(reqItem.getProductId());
                Item item = new Item();
                item.setProduct(product);
                item.setQuantity(reqItem.getQuantity());
                item.calculateSubtotal();
                item.setCart(cart);
                items.add(item);
            }
            cart.setItems(items);
            cart.calculateTotal();

            return ResponseEntity.ok(caMapper.toResponse(caService.update(id, cart))); // 200

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User not found: " + e.getMessage()); // 404
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Cart or product not found: " + e.getMessage()); // 404
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("Invalid data: " + e.getMessage()); // 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            caService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Cart not found: " + e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCartById(@PathVariable Long id) {
        try {
            Cart cart = caService.findById(id);
            return ResponseEntity.ok(caMapper.toResponse(cart)); // 200

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Cart not found: " + e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getAllCartByUsername(@PathVariable String username) {
        try {
            List<Cart> carts = caService.findByUsername(username);
            return ResponseEntity.ok(caMapper.toResponseList(carts)); // 200

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }
}