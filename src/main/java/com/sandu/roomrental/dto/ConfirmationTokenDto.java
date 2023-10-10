package com.sandu.roomrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ConfirmationTokenDto {
    @NotBlank
    @NotNull
    String token;
    @NotBlank
    @NotNull
    String email;


}
