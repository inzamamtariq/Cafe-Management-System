package com.cafe.management.DAO;

import com.cafe.management.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByDateAndTimeAndTableNumber(LocalDate date, LocalTime time, int tableNumber);

    List<Reservation> findByStatus(String available);
}
