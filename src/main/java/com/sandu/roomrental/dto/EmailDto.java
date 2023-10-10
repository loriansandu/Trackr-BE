package com.sandu.roomrental.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailDto {
    @Email
    String email;
}
