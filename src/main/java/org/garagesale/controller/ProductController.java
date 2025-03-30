package org.garagesale.controller;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.garagesale.payload.ApiResponse;
import org.garagesale.payload.ImageDTO;
import org.garagesale.payload.ProductDTO;
import org.garagesale.payload.ProductResponse;
import org.garagesale.payload.ProductUpdateDTO;
import org.garagesale.security.jwt.JwtUtils;
import org.garagesale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    public ProductService productService;

//    @PostMapping("/me/product")
//    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO productDTO) {
//
//        ProductDTO savedProduct = productService.addProduct(productDTO);
//        return new ResponseEntity<>(ApiResponse.withPayload(savedProduct), HttpStatus.CREATED);
//    }

    @PostMapping(value = "/me/product", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("specialPrice") double specialPrice,
            @RequestParam("quantity") int quantity,
            @RequestPart("images") List<MultipartFile> images) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName(productName);
        productDTO.setDescription(description);
        productDTO.setPrice(price);
        productDTO.setSpecialPrice(specialPrice);
        productDTO.setQuantity(quantity);

        ProductDTO savedProductDTO = productService.addProductWithImages(productDTO, images);
        return ResponseEntity.ok(ApiResponse.withPayload(savedProductDTO));
    }

    @PostMapping("/me/product/{productId}/image")
    public ResponseEntity<ApiResponse<?>> uploadImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) {
        ImageDTO savedImage = productService.uploadProductImage(productId, image);
        return ResponseEntity.ok(ApiResponse.withPayload(savedImage));
    }

    @GetMapping("/me/products")
    public ResponseEntity<ApiResponse<ProductResponse>> getAllUserProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "productId", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc", required = false) String sortOrder
    ) {

        ProductResponse productResponse = productService.getAllUserProducts(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(ApiResponse.withPayload(productResponse), HttpStatus.OK);
    }

    @DeleteMapping("/me/product/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> deleteProduct(@PathVariable Long productId) {

        ProductDTO deletedProduct = productService.deleteProduct(productId);

        return new ResponseEntity<>(ApiResponse.withPayload(deletedProduct), HttpStatus.OK);
    }

    @PutMapping("/me/product/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO, @PathVariable Long productId) {

        ProductDTO updatedProduct = productService.updateProduct(productId, productUpdateDTO);

        return new ResponseEntity<>(ApiResponse.withPayload(updatedProduct), HttpStatus.OK);
    }

}
