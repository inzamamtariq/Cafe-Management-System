package com.cafe.management.restImpl;

import com.cafe.management.constants.CafeConstants;
import com.cafe.management.model.Bill;
import com.cafe.management.service.BillService;
import com.cafe.management.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    BillService billService;

    @PostMapping("/generateReport")
    public ResponseEntity<String> generateBill(@RequestBody(required = true)Map<String,Object> requestMap){
        try {
            return billService.generateBill(requestMap);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/getBills")
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @PostMapping(path = "/getPdf")
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @PostMapping(path = "/deleteBill/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Integer id) {
        try {
            return billService.deleteBill(id);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
