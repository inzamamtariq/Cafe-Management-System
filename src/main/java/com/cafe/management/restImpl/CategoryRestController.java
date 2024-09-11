package com.cafe.management.restImpl;

import com.cafe.management.constants.CafeConstants;
import com.cafe.management.model.Category;
import com.cafe.management.service.CategoryService;
import com.cafe.management.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryRestController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return categoryService.addNewCategory(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue) {
        try {
            return categoryService.getAllCategory(filterValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return categoryService.updateCategory(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}