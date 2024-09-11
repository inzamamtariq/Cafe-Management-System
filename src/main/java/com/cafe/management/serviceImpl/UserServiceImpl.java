package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.UserDAO;
import com.cafe.management.JWT.CustomerUserDetailsService;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.JWT.JwtUtils;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.model.User;
import com.cafe.management.service.UserService;
import com.cafe.management.utils.CafeUtils;
import com.cafe.management.utils.MailUtils;
import com.cafe.management.wrapper.UserWrapper;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    MailUtils mailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp{}" + requestMap);
        try {
            if (validateRequestMap(requestMap)) {
                User user = userDAO.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDAO.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Registered Successfully.", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.EMAIL_ALREADY_EXSIST, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.BAD_REQUEST);
    }

    private boolean validateRequestMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email")
                && requestMap.containsKey("password") && requestMap.containsKey("contactNumber")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();

        user.setName(requestMap.get("name"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setRole("user");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside Login");
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\""
                            + jwtUtils.generateToken(customerUserDetailsService.getUserDetail().getEmail()
                            , customerUserDetailsService.getUserDetail().getRole()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait For Admin Approval" + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("Error in login", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUSer() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()) {
                    userDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDAO.getAllAdmin());
                    return CafeUtils.getResponseEntity("User Status is updated", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("ID Doesn't exsist ", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "User:- " + user + " \n is Approved by \n Admin:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "User:- " + user + " \n is Disabled by \n Admin:-" + jwtFilter.getCurrentUser(), allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDAO.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)) {
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDAO.save(userObj);
                    return CafeUtils.getResponseEntity("Password Cahnged Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Old Password is not matched", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmailId(requestMap.get("email"));
            if (!Objects.isNull(user) && !StringUtils.isEmpty(user.getEmail())) {
                mailUtils.forgotPassword(requestMap.get("email"), "Your Credentials from Cafe Management are as:", user.getPassword());
            } else {
                return CafeUtils.getResponseEntity("Check Your mail for the credentials", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> addEmployee(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateRequestMapForEmployee(requestMap)) {
                    User user = userDAO.findByEmailId(requestMap.get("email"));
                    if (Objects.isNull(user)) {
                        userDAO.save(getEmployeeFromMap(requestMap));
                        return CafeUtils.getResponseEntity("Employee Added Successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Employee Already Exists",HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> deleteEmployee(int id) {
        try {
            if (jwtFilter.isAdmin()){
                User user = userDAO.findById(id).get();
                if (Objects.isNull(user)){
                    return CafeUtils.getResponseEntity("Employee not Found on Given ID",HttpStatus.NOT_FOUND);
                } else {
                    userDAO.deleteById(id);
                    return CafeUtils.getResponseEntity("Employee Deleted Successfully",HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllEmployee() {
        try {
            if (jwtFilter.isAdmin()){
                List<UserWrapper> result = userDAO.getAllEmployee();
                return new ResponseEntity<>(result,HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private User getEmployeeFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setRole("Employee");
        user.setName(requestMap.get("name"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("true");
        return user;
    }

    private boolean validateRequestMapForEmployee(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email")
                && requestMap.containsKey("password") && requestMap.containsKey("contactNumber")) {
            return true;
        } else {
            return false;
        }
    }
}
