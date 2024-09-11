package com.cafe.management.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ShoppingCartService {
    ResponseEntity<String> addItemToCart(Map<String,String> shoppingCart);
}
