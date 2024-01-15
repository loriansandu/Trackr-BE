package com.sandu.trackr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class EditAppointmentDto {
   @NotEmpty @NotNull @Valid
   AppointmentDto selectedAppointment;

   @NotEmpty @NotNull @Valid
   Date date;
}
