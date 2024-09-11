package com.cafe.management.restImpl;

import com.cafe.management.model.Reservation;

import com.cafe.management.serviceImpl.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return new ResponseEntity<>(reservationService.createReservation(reservation), HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Reservation>> getReservations() {
        return new ResponseEntity<>(reservationService.getReservations(), HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) throws Exception {
        return new ResponseEntity<>(reservationService.updateReservation(id, reservation), HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/tableAvailable")
    public ResponseEntity<List<Reservation>> getAvailableTables() {
        return new ResponseEntity<>(reservationService.getAvailableTables(), HttpStatus.OK);
    }
}