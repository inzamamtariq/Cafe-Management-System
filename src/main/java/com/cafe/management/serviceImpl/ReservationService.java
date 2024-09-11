package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.ReservationRepository;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.model.Reservation;
import com.cafe.management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    JwtFilter jwtFilter;

    public Reservation createReservation(Reservation reservation) {
        List<Reservation> existingReservations = reservationRepository.findByDateAndTimeAndTableNumber(reservation.getDate(), reservation.getTime(), reservation.getTableNumber());
        if (!existingReservations.isEmpty()) {
            throw new IllegalStateException("Table is not available for reservation");
        }
        reservation.setStatus("RESERVED");
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public Reservation updateReservation(Long id, Reservation reservation) throws Exception {
        Reservation existingReservation = reservationRepository.findById(id).orElseThrow(() -> new Exception("Reservation not found"));
        existingReservation.setDate(reservation.getDate());
        existingReservation.setTime(reservation.getTime());
        existingReservation.setNumberOfGuests(reservation.getNumberOfGuests());
        existingReservation.setCustomerName(reservation.getCustomerName());
        existingReservation.setCustomerContact(reservation.getCustomerContact());
        return reservationRepository.save(existingReservation);
    }

    public void cancelReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getAvailableTables() {
        return reservationRepository.findByStatus("AVAILABLE");
    }

    public ResponseEntity<String> eReserveTable(Reservation reservation) {
        try {
            if (jwtFilter.isEmployee()) {
                List<Reservation> existingReservations = reservationRepository.findByDateAndTimeAndTableNumber(reservation.getDate(), reservation.getTime(), reservation.getTableNumber());
                if (!existingReservations.isEmpty()) {
                    return CafeUtils.getResponseEntity("Table is not available for the given Time", HttpStatus.BAD_REQUEST);
                } else {
                    reservation.setStatus("RESERVED");
                    reservationRepository.save(reservation);
                    return CafeUtils.getResponseEntity("Reservation Confirmed Successfully", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity("You are not an Employee", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<Reservation>> getReservationsEmployee() {
        try {
            if (jwtFilter.isEmployee()) {
                return new ResponseEntity<>(reservationRepository.findByStatus("AVAILABLE"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
