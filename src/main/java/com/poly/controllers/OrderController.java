package com.poly.controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.poly.models.entities.Account;
import com.poly.models.entities.Order;
import com.poly.models.entities.OrderDetail;
import com.poly.models.entities.Product;
import com.poly.models.mappers.OrderMapper;
import com.poly.models.requests.OrderDetailRequest;
import com.poly.models.requests.OrderRequest;
import com.poly.models.services.AccountService;
import com.poly.models.services.EmailService;
import com.poly.models.services.OrderService;
import com.poly.models.services.ProductService;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
	
	private final EmailService eService;
    private final OrderService oService;
    private final AccountService aService;
    private final ProductService pService;
    private final OrderMapper oMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequest request) {
        try {
            Order order = oMapper.toEntity(request);
            Account account = aService.findById(request.getUsername());
            order.setAccount(account);
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (OrderDetailRequest odr : request.getOrderDetails()) {
                Product product = pService.findById(odr.getProductId());
                if (product.getAmount() - odr.getQuantity() < 0) {
                	throw new IllegalArgumentException("Not enough product in stock: " + product.getName());
                }
                product.setAmount(product.getAmount() - odr.getQuantity());
                if (product.getAmount() == 0) {
                	pService.update(product.getId(), product);
                	throw new IllegalArgumentException("Product is out of stock " + product.getName());
                }
                product.setSales(product.getSales() + odr.getQuantity());
                pService.update(odr.getProductId(), product);

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(product);
                orderDetail.setPrice(product.getRetailPrice());
                orderDetail.setQuantity(odr.getQuantity());
                orderDetail.calculateSubtotal();
                orderDetails.add(orderDetail);
            }
            order.setOrderDetails(orderDetails);
            order.calculateTotal();

            eService.sendEmailWithInlineImage(account.getEmail(), order);
            
            Order saved = oService.create(order);
            
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();
            
            return ResponseEntity.created(location).body(oMapper.toResponse(saved)); // 201

        } catch (MessagingException e) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Can't send email: " + e.getMessage()); // 404
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
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody OrderRequest request, @PathVariable Long id) {
        try {
        	System.out.println("Data: " + request);
            Order order = oService.findById(id);
            order.setStatus(request.getStatus());
            order.setPhone(request.getPhone());
            order.setAddress(request.getAddress());
            order.setFullname(request.getFullname());
            return ResponseEntity.ok(oMapper.toResponse(oService.update(id, order))); // 200

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User not found: " + e.getMessage()); // 404

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Order or product not found: " + e.getMessage()); // 404

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("Invalid data: " + e.getMessage()); // 400

        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllThenFilterAndPaginate(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullname,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize) {
        try {
        	System.out.println(fullname);
            List<Order> orders = oService.filteredAndPaginated(username, fullname, fromDate, toDate, page, pageSize);
            return ResponseEntity.ok(oMapper.toResponseList(orders)); // 200

        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }
    
    @GetMapping("/total-pages") // added missing @GetMapping
    public ResponseEntity<?> getTotalPages(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullname,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "8") Integer pageSize) {
        try {
            if (pageSize <= 0) {
                return ResponseEntity.badRequest()
                                     .body("Page size must be greater than 0"); // 400
            }
            long totalRows = oService.countFilteredOrders(username, fullname, fromDate, toDate);
            int totalPages = (int) Math.ceil((double) totalRows / pageSize);
            return ResponseEntity.ok(totalPages); // 200

        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }
}
