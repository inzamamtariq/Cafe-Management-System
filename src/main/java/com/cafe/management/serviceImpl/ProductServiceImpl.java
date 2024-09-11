package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.ProductDao;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.model.Category;
import com.cafe.management.model.Product;
import com.cafe.management.service.ProductService;
import com.cafe.management.utils.CafeUtils;
import com.cafe.management.wrapper.ProductWrapper;
import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.logging.Logger;


@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    ProductDao productDao;

    private static long ONE_THOUSAND = 1000L;

    private static final int BYTE_SIZE = 1024;
    private static final int INT_ZERO = 0;
    private static final int INT_MINUS_ONE = -1;
    private static final int INT_ONE = 1;
    private static final int INT_YEAR = 1970;
    private static final int DAY_NUM = 86400;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateRequestMap(requestMap, false)) {
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product added Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateRequestMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if (isAdd) {
            product.setId(Integer.parseInt(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        product.setDescription(requestMap.get("description"));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateRequestMap(requestMap, true)) {
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()) {
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Product Id doesn't exsist", HttpStatus.OK);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProductById(Integer id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(id);
                if (optional.isPresent()) {
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product deleted Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Product with given Id not found", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()) {
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Product with given Id not found", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getProductByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
