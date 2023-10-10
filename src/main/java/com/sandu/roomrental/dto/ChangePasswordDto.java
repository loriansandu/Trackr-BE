package com.sandu.roomrental.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    String password;
    String newPassword;
}
