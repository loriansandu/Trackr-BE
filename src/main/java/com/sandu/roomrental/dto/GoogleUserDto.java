package com.sandu.roomrental.dto;

import lombok.Data;

@Data
public class GoogleUserDto {
    private String idToken;
    private String id;
    private String name;
    private String email;
    private String photoUrl;
}

