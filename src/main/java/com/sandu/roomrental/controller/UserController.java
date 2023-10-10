package com.sandu.roomrental.controller;


import com.sandu.roomrental.dto.ChangeNameDto;
import com.sandu.roomrental.dto.ChangePasswordDto;
import com.sandu.roomrental.dto.UserInfoDto;
import com.sandu.roomrental.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserInfoDto> getUser() {
//        return this.userService.getUsername();
//        Map<String, String> response = new HashMap<>();
//        response.put("username", this.userService.getLoggedUserData());
        return ResponseEntity.ok(this.userService.getLoggedUserData());
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        String response = this.userService.changePassword(changePasswordDto);
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PutMapping("/change-number")
    public ResponseEntity<String> changeNumber(@RequestBody String number) {
        System.out.println(number);
        String response = this.userService.changeNumber(number);
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PutMapping("/change-name")
    public ResponseEntity<String> changeNames(@RequestBody ChangeNameDto changeNameDto) {
        String response = this.userService.changeNames(changeNameDto.getFirstName(), changeNameDto.getLastName());
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PutMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestBody MultipartFile file) {

        String response = this.userService.uploadProfilePicture(file);
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @DeleteMapping("/delete-profile-picture")
    public ResponseEntity<String> removeProfilePicture() {

        String response = this.userService.removeProfilePicture();
        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }


}
