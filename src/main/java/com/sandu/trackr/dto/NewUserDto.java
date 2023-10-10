package com.sandu.trackr.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserDto {
    @NotBlank
    String email;
    @NotBlank
    String password;
}
