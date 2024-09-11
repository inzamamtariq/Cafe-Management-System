package com.cafe.management.service;

import com.cafe.management.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProduct();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProductById(Integer id);

    ResponseEntity<String> updateStatus(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getProductByCategory(Integer id);

    ResponseEntity<ProductWrapper> getProductById(Integer id);
}
