package com.sandu.trackr.controller;


import com.sandu.trackr.dto.*;
import com.sandu.trackr.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {


    private final AppointmentService appointmentService;

    @GetMapping("/last-appointment-number")
    public ResponseEntity<Long> getLastAppointmentNumber() {
//        return this.userService.getUsername();
//        Map<String, String> response = new HashMap<>();
//        response.put("username", this.userService.getLoggedUserData());
        return ResponseEntity.ok(this.appointmentService.getLastAppointmentNumber());
    }

    @GetMapping("/date")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDate(@RequestParam Date date) {
        return ResponseEntity.ok(this.appointmentService.getAppointmentsByDate(date));
    }

    @GetMapping("/week")
    public ResponseEntity<List<AppointmentDto>> getThisWeekAppointments(@RequestParam Date date) {
        return ResponseEntity.ok(this.appointmentService.getAppointmentsByWeek(date));
    }

    @PostMapping("/create-appointment")
    public ResponseEntity<String> createAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
        String response = this.appointmentService.createAppointment(appointmentDto);
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PutMapping("/edit-appointment")
    public ResponseEntity<String> editAppointment(@RequestBody @Valid EditAppointmentDto editAppointmentDto) {
        String response = this.appointmentService.editAppointment(editAppointmentDto.getSelectedAppointment(), editAppointmentDto.getDate());
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @DeleteMapping("/delete-appointment")
    public ResponseEntity<String> deleteAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
        String response = this.appointmentService.deleteAppointment(appointmentDto);
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PostMapping("/check-dates-are-available")
    public ResponseEntity<Void> checkAvailableDates(@RequestBody @Valid AppointmentDto[] appointmentDto) {
        this.appointmentService.checkAvailableDates(appointmentDto);
        return ResponseEntity.ok(null);
    }

}
