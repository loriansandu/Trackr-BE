package com.sandu.trackr.repository;

import com.sandu.trackr.model.Appointment;
import com.sandu.trackr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByUserAndDate(User user, Date date);

    Optional<Appointment> findTopByUser(User user);

    Optional<Appointment> findTopByUserOrderByAppointmentNumberDesc(User user);

    List<Appointment> findAllByUserOrderByDate(User user);

}
