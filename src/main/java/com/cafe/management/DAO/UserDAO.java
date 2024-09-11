package com.cafe.management.DAO;

import com.cafe.management.model.User;
import com.cafe.management.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUser();

    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<String> getAllAdmin();

    User findByEmail(String email);

    List<UserWrapper> getAllEmployee();
}
