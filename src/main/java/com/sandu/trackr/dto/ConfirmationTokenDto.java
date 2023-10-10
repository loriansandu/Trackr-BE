package com.sandu.trackr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
