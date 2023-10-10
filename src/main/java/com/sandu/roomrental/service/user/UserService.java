package com.sandu.roomrental.service.user;

import com.sandu.roomrental.dto.ChangePasswordDto;
import com.sandu.roomrental.dto.ConfirmationTokenDto;
import com.sandu.roomrental.dto.NewUserDto;
import com.sandu.roomrental.dto.UserInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    String register(NewUserDto newUser);
    Map<String, String> login(NewUserDto user);
    String confirmEmail(ConfirmationTokenDto confirmationToken);

    UserInfoDto getLoggedUserData();

    String resendVerificationCode(String email);

    String resetPassword(String email);

    Map<String, String> loginWithGoogle(String idToken);

    String changePassword(ChangePasswordDto changePasswordDto);

    String changeNumber(String number);

    String changeNames(String firstName, String lastName);

    String uploadProfilePicture(MultipartFile file);

    String removeProfilePicture();

}
