package com.sandu.trackr.service.appointment;

import com.sandu.trackr.dto.*;

import java.util.Date;
import java.util.List;

public interface AppointmentService {

    Long getLastAppointmentNumber();

    void checkAvailableDates(AppointmentDto[] datesListDto);

    String createAppointment(AppointmentDto appointmentDto);

    List<AppointmentDto> getAppointmentsByDate(Date date);

    List<AppointmentDto> getAppointmentsByWeek(Date date);

    String editAppointment(AppointmentDto appointmentDto, Date date);

    String deleteAppointment(AppointmentDto appointmentDto);
}
