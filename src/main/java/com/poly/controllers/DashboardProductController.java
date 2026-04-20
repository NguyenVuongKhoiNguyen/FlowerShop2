package com.poly.controllers;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.poly.models.entities.Product;
import com.poly.models.mappers.ProductMapper;
import com.poly.models.requests.ProductRequest;
import com.poly.models.services.ProductService;
import com.poly.utils.ImageUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/dashboard/products")
@RestController
@RequiredArgsConstructor
public class DashboardProductController {

    private final ProductMapper pMapper;
    private final ProductService pService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart(value = "product") ProductRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Product p = pMapper.toEntity(request);

            // Handle image
            String imageName = "lost.jpg";
            if (file != null && !file.isEmpty()) {
                imageName = ImageUtil.saveImage(file);
            }
            p.setImage(imageName);                              // always set

            Product saved = pService.create(p);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(pMapper.toResponse(saved)); // 201

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body("Invalid data: " + e.getMessage()); // 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @RequestPart(value = "product") ProductRequest request,
            @PathVariable Integer id,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Product p = pMapper.toEntity(request);
            p.setId(id);

            // Handle image safely
            String oldImage = pService.findById(id).getImage();
            String newImage = oldImage;
            if (file != null && !file.isEmpty()) {
                newImage = ImageUtil.saveImage(file);           // save new first
            }
            p.setImage(newImage);

            Product updated = pService.update(id, p);

            if (file != null && !file.isEmpty()) {
                ImageUtil.deleteImage(oldImage);                // delete old only after success
            }

            return ResponseEntity.ok(pMapper.toResponse(updated)); // 200

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        try {
            String imageName = pService.findById(id).getImage();
            pService.deleteById(id);
            ImageUtil.deleteImage(imageName);                   // delete image only after success

            return ResponseEntity.noContent().build(); // 204

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Product not found: " + e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ho Lee Fuk"); // 500
        }
    }
}
