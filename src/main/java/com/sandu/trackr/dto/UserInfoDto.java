package com.sandu.trackr.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private String email;
    private String firstName;
    private String lastName;
    private String pictureUrl;
    private String phoneNumber;
    private String profilePicture;
    private String fileType;
}
