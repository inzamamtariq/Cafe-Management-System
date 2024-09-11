package com.cafe.management.DAO;

import com.cafe.management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();

}
