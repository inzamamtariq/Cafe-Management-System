package com.cafe.management.DAO;

import com.cafe.management.model.Bill;
import com.cafe.management.model.analysis.Analysis1;
import com.cafe.management.model.analysis.Analysis2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public interface BillDao extends JpaRepository<Bill, Integer> {

    List<Bill> getAllBills();

    List<Bill> getBillByUserName(@Param("username") String username);


    @Query("select new com.cafe.management.model.analysis.Analysis1(b.createdBy as name, count(b.createdBy)) from Bill b group by b.createdBy")
    List<Analysis1> getAllOrderCountByUser();

    @Query("select new com.cafe.management.model.analysis.Analysis1(b.createdBy as name, sum(b.total) as value) from Bill b group by b.createdBy")
    List<Analysis1> getSpentMoneyTotalAll();

    @Query(value = "SELECT product_name, SUM(value) as total_sales FROM (SELECT JSON_UNQUOTE(JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].name'))) as product_name, JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].total')) as value FROM bill JOIN (SELECT 0 as idx UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) as idx WHERE idx < JSON_LENGTH(bill.productdetail)) as subquery GROUP BY product_name", nativeQuery = true)
    List<Object[]> getProductSaleAll();

    default List<Analysis2> getProductSaleAllAsAnalysis2() {
        return getProductSaleAll().stream()
                .map(row -> new Analysis2((String) row[0], (Double) row[1]))
                .collect(Collectors.toList());
    }

    @Query(value = "SELECT product_name, SUM(value) as total_sales FROM (SELECT JSON_UNQUOTE(JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].category'))) as product_name, JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].total')) as value FROM bill JOIN (SELECT 0 as idx UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) as idx WHERE idx < JSON_LENGTH(bill.productdetail)) as subquery GROUP BY product_name", nativeQuery = true)
    List<Object[]> getCategorySaleAll();

    default List<Analysis2> getCategorySaleAllAsAnalysis2() {
        return getCategorySaleAll().stream()
                .map(row -> new Analysis2((String) row[0], (Double) row[1]))
                .collect(Collectors.toList());
    }

}
