package com.sandu.trackr.service.user;

import com.sandu.trackr.dto.ChangePasswordDto;
import com.sandu.trackr.dto.ConfirmationTokenDto;
import com.sandu.trackr.dto.NewUserDto;
import com.sandu.trackr.dto.UserInfoDto;
import com.sandu.trackr.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    String register(NewUserDto newUser);
    Map<String, String> login(NewUserDto user);
    String confirmEmail(ConfirmationTokenDto confirmationToken);

    UserInfoDto getLoggedUserData();

    User getUser();

    String resendVerificationCode(String email);

    String resetPassword(String email);

    Map<String, String> loginWithGoogle(String idToken);

    String changePassword(ChangePasswordDto changePasswordDto);

    String changeNumber(String number);

    String changeNames(String firstName, String lastName);

    String uploadProfilePicture(MultipartFile file);

    String removeProfilePicture();

    void log(HttpServletRequest request);
}
