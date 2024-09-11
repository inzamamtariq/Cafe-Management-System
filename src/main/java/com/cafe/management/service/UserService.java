package com.cafe.management.service;

import com.cafe.management.model.User;
import com.cafe.management.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUSer();

    ResponseEntity<String> updateUser(Map<String, String> requestMap);

    ResponseEntity<String> checkToken();

    ResponseEntity<String> changePassword(Map<String, String> requestMap);

    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

    ResponseEntity<String> addEmployee(Map<String, String> requestMap);

    ResponseEntity<String> deleteEmployee(int id);

    ResponseEntity<List<UserWrapper>> getAllEmployee();
}
