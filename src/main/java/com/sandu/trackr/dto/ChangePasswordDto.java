package com.sandu.trackr.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    String password;
    String newPassword;
}
