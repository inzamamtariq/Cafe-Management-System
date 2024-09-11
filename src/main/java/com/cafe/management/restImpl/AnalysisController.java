package com.cafe.management.restImpl;

import com.cafe.management.model.analysis.Analysis1;
import com.cafe.management.model.analysis.Analysis2;
import com.cafe.management.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    BillService billService;

    @PostMapping("/getAllOrderCountByName")
    public ResponseEntity<List<Analysis1>> getAllOrderCountByUser(){
        try {
            return billService.getAllOrderCountByUser();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getSpentMoneyTotalAll")
    public ResponseEntity<List<Analysis1>> getSpentMoneyTotalAll(){
        try {
            return billService.getSpentMoneyTotalAll();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getProductSaleAll")
    public ResponseEntity<List<Analysis2>> getProductSaleAll(){
        try {
            return billService.getProductSaleAll();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getCategorySaleAll")
    public ResponseEntity<List<Analysis2>> getCategorySaleAll(){
        try {
            return billService.getCategorySaleAll();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
