package com.sandu.trackr.controller;

import com.sandu.trackr.dto.*;
import com.sandu.trackr.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody NewUserDto newUserDto) {

        String response = String.valueOf(userService.register(newUserDto));

        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody NewUserDto newUserDto) {

        Map<String, String> response = userService.login(newUserDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody String idToken) {

        Map<String, String> response = userService.loginWithGoogle(idToken);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/validate")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<String> confirmUserAccount(@Valid @RequestBody ConfirmationTokenDto confirmationToken) {

        String response =  userService.confirmEmail(confirmationToken);

        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PostMapping("/resend-verification-code")
    public ResponseEntity<String> resendVerificationCode(@Valid @RequestBody EmailDto email) {

        String response =  userService.resendVerificationCode(email.getEmail());

        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody EmailDto email) {

        String response =  userService.resetPassword(email.getEmail());

        return ResponseEntity.ok("{\"message\": \"" + response + "\"}");
    }



}
