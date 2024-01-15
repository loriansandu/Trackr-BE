package com.sandu.trackr.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AppointmentDto {
   @NotEmpty @NotNull
   String title;

   @NotEmpty @NotNull
   String trainer;

   @NotEmpty @NotNull
   Date date;
   String hour;
   String minute;
   Number number;

   public AppointmentDto(String title, String trainer, Date date, Number number) {
      this.title = title;
      this.trainer = trainer;
      this.date = date;
      this.number = number;
   }
}
