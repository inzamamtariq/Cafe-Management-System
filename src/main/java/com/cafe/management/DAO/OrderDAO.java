package com.cafe.management.DAO;

import com.cafe.management.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface OrderDAO extends JpaRepository<Order,Integer> {
}
