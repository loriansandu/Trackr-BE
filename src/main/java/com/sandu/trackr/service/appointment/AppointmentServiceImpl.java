package com.sandu.trackr.service.appointment;

import com.sandu.trackr.dto.*;
import com.sandu.trackr.exception.AppointmentNotFoundException;
import com.sandu.trackr.exception.DateUnavailableException;
import com.sandu.trackr.model.Appointment;
import com.sandu.trackr.model.User;
import com.sandu.trackr.repository.AppointmentRepository;
import com.sandu.trackr.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    @Override
    public Long getLastAppointmentNumber() {
        var user = userService.getUser();
        Appointment lastAppointment = appointmentRepository.findTopByUserOrderByAppointmentNumberDesc(user).orElse(new Appointment());
        System.out.println(lastAppointment);
        return lastAppointment.getAppointmentNumber();
    }

    @Override
    public void checkAvailableDates(AppointmentDto[] appointments) {
        User user = userService.getUser();
        List<String> unavailableDates = new ArrayList<>();
        for(AppointmentDto appointment : appointments) {
            System.out.println(appointment);
            appointmentRepository.findByUserAndDate(user, appointment.getDate()).ifPresent(s -> {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                unavailableDates.add(dateFormat.format(appointment.getDate()));
            });
        }
        if (!unavailableDates.isEmpty()) {
            throw new DateUnavailableException(unavailableDates.toString(), unavailableDates);
        }
    }

    @Override
    public String createAppointment(AppointmentDto appointmentDto) {
        var user = userService.getUser();
        appointmentRepository.findByUserAndDate(user, appointmentDto.getDate()).ifPresent(s -> {
//            DateFormat dateFormat = new SimpleDateFormat();
//            dateFormat.format(appointmentDto.getDate());
//            throw new DateUnavailableException(dateFormat.toString());
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointmentDto.getDate());
        calendar.set(Calendar.MILLISECOND, 0);

        Appointment appointment = new Appointment();
        appointment.setDate(calendar.getTime());
        appointment.setUser(user);
        appointment.setTitle(appointmentDto.getTitle());
        appointment.setTrainer(appointmentDto.getTrainer());
        appointment.setAppointmentNumber(getLastAppointmentNumber() + 1);
        appointmentRepository.save(appointment);
        return "Appointment created";
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDate(Date date) {
        var user = userService.getUser();
        LocalDate selectedDay = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<AppointmentDto> appointments = new ArrayList<>();
        for (Appointment appointment : this.appointmentRepository.findAllByUserOrderByDate(user)) {
            LocalDate appointmentLocalDate = appointment.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(selectedDay.isEqual(appointmentLocalDate)) {
                appointments.add(new AppointmentDto(appointment.getTitle(), appointment.getTrainer(), appointment.getDate(), appointment.getAppointmentNumber()));
            }
        }
        return appointments;

    }

    @Override
    public List<AppointmentDto> getAppointmentsByWeek(Date date) {
        System.out.println(date);
        var user = userService.getUser();
        int weekOfYearToday = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);;
        System.out.println(weekOfYearToday);
        List<AppointmentDto> appointments = new ArrayList<>();
        for (Appointment appointment : this.appointmentRepository.findAllByUserOrderByDate(user)) {
            LocalDate appointmentLocalDate = appointment.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int weekOfYearAppointmentDate = appointmentLocalDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            if(weekOfYearToday == weekOfYearAppointmentDate) {
                System.out.println(weekOfYearAppointmentDate);
                appointments.add(new AppointmentDto(appointment.getTitle(), appointment.getTrainer(), appointment.getDate(), appointment.getAppointmentNumber()));
            }
        }
        return appointments;
    }

    @Override
    public String editAppointment(AppointmentDto appointmentDto, Date date) {
        var user = userService.getUser();
        Appointment appointment = appointmentRepository.findByUserAndDate(user, date).orElseThrow(() ->
            new AppointmentNotFoundException("Appointment not found"));

        appointment.setDate(appointmentDto.getDate());
        appointment.setTitle(appointmentDto.getTitle());
        appointment.setTrainer(appointmentDto.getTrainer());
        System.out.println(appointment);
        appointmentRepository.save(appointment);
        return "Appointment modified";
    }

    @Override
    public String deleteAppointment(AppointmentDto appointmentDto) {
        var user = userService.getUser();
        Appointment appointment = appointmentRepository.findByUserAndDate(user, appointmentDto.getDate()).orElseThrow(() ->
                new AppointmentNotFoundException("Appointment not found"));

        appointmentRepository.delete(appointment);
        return "Appointment deleted";
    }
}
