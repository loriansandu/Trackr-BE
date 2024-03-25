package com.sandu.trackr.service.user;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sandu.trackr.dto.ChangePasswordDto;
import com.sandu.trackr.dto.ConfirmationTokenDto;
import com.sandu.trackr.dto.NewUserDto;
import com.sandu.trackr.dto.UserInfoDto;
import com.sandu.trackr.exception.*;
import com.sandu.trackr.model.ConfirmationToken;
import com.sandu.trackr.model.Role;
import com.sandu.trackr.model.User;
import com.sandu.trackr.repository.ConfirmationTokenRepository;
import com.sandu.trackr.repository.UserRepository;
import com.sandu.trackr.security.service.JwtService;
import com.sandu.trackr.service.email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    @Value("${google.client-id}")
    private String googleId;
    @Value("${SPRING_MAIL_USERNAME}")
    private String logEmail;


    @Override
    public String register(NewUserDto newUserDto) {
        Optional<User> user = userRepository.findByEmail(newUserDto.getEmail());
        if(user.isPresent())
            throw new EmailAlreadyExistsException("Email already exists");

        User newUser = new User();

        newUser.setEmail(newUserDto.getEmail());

        newUser.setPassword(passwordEncoder.encode(newUserDto.getPassword()));

        newUser.setRole(Role.ROLE_USER);

        userRepository.save(newUser);

        ConfirmationToken confirmationToken = new ConfirmationToken(newUser);
        confirmationTokenRepository.save(confirmationToken);

        sendEmail(newUser, "Complete Registration!", "Your code is: %d Use it to verify your account.", confirmationToken.getConfirmationToken());

        return "Verify email by the link sent on your email address";

    }

    private void sendEmail(User newUser, String title, String message, Object value) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newUser.getEmail());
        mailMessage.setSubject(title);
        mailMessage.setText(String.format(message, value));
        emailService.sendEmail(mailMessage);
    }

    @Override
    public Map<String, String> login(NewUserDto user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            if(e.getMessage().contains("User is disabled"))
                throw new AccountNotVerifiedException("Account not verified");
            throw new BadCredentialsException("Bad credentials");
        }
        User existingUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info(existingUser.getEmail() + "logged in");
        Map<String, String> data = new HashMap<>();
        data.put("token", jwtService.generateToken(existingUser));
        return data;
    }

    @Override
    public String confirmEmail(ConfirmationTokenDto confirmationToken) {
        User user = userRepository.findByEmail(confirmationToken.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Wrong email"));
        if (user.isEnabled()) {
            throw new AccountAlreadyVerifiedException("Your account is already verified");
        }
        confirmationTokenRepository.findByConfirmationTokenAndUser(Integer.parseInt(confirmationToken.getToken()), user).orElseThrow(() -> new TokenException("Wrong verification code"));
        user.setEnabled(true);
        userRepository.save(user);
        return "Email verified successfully!";
    }

    @Override
    public UserInfoDto getLoggedUserData() {
        var user = getUser();
        byte[] profilePicture = user.getProfilePicture();
        String base64ProfilePicture = "";
        if (profilePicture != null) {
            base64ProfilePicture = Base64.getEncoder().encodeToString(profilePicture);
        }
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setFirstName(user.getFirstName());
        userInfoDto.setLastName(user.getLastName());
        userInfoDto.setPictureUrl(user.getPictureUrl());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setPhoneNumber(user.getPhoneNumber());
        userInfoDto.setProfilePicture(base64ProfilePicture);
        userInfoDto.setFileType(user.getFileType());
        return userInfoDto;
    }

    @Override
    public User getUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
//        return (User) authentication.getPrincipal();
        return (User) authentication.getPrincipal();

    }

    @Override
    public String resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Wrong email"));
        if (user.isEnabled()) {
            throw new AccountAlreadyVerifiedException("Your account is already verified");
        }
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByUser(user);
        confirmationToken.ifPresent(confirmationTokenRepository::delete);

        ConfirmationToken newConfirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(newConfirmationToken);

        sendEmail(user, "Your Verification Code", "Your code is: %d Use it to verify your account.", newConfirmationToken.getConfirmationToken());

        return "New code sent!";
    }

    @Override
    public String resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Wrong email"));
        String newPassword = generateCommonLangPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        sendEmail(user, "Password reset successful!", "Your new password is: %s Use it to login into your account.", newPassword);

        return "New password was sent on email!";
    }

    @Override
    public Map<String, String> loginWithGoogle(String idToken) {

        GoogleIdToken idTokenReceived = null;
        try {
            idTokenReceived = verifyGoogleIdToken(idToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleLoginException(e);
        }
        if (idTokenReceived != null) {
            Payload payload = idTokenReceived.getPayload();
            log.info(payload.getEmail() + "logged in");
            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user = existingUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setRole(Role.ROLE_USER);
                newUser.setEnabled(true);
                newUser.setFirstName(givenName);
                newUser.setLastName(familyName);
                newUser.setPictureUrl(pictureUrl);
                newUser.setPassword(passwordEncoder.encode("1234"));
                userRepository.save(newUser);
                return newUser;
            });

            Map<String, String> data = new HashMap<>();
            data.put("token", jwtService.generateToken(user));
            return data;

        } else {
            throw new BadCredentialsException("Google authentication failed!");
        }

    }

    @Override
    public String changePassword(ChangePasswordDto changePasswordDto) {
        User user = getUser();
        if (passwordEncoder.matches(changePasswordDto.getPassword(),user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return "Password was changed successfully";
        }
        else {
            throw new WrongPasswordException("The password is incorrect");
        }
    }

    @Override
    public String changeNumber(String number) {
        User user = getUser();
        user.setPhoneNumber(number);
        userRepository.save(user);
        return "Phone number was changed successfully";
    }

    @Override
    public String changeNames(String firstName, String lastName) {
        User user = getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userRepository.save(user);
        return "Name was changed successfully";
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        User user = getUser();
        try {
            user.setProfilePicture(file.getBytes());
            user.setFileType(file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.save(user);
        return "Photo was uploaded successfully";
    }

    @Override
    public String removeProfilePicture() {
        User user = getUser();
        if(user.getProfilePicture() != null) {
            user.setProfilePicture(null);
        }
        else {
            user.setPictureUrl(null);
        }
        userRepository.save(user);
        return "Photo deleted";
    }

    @Override
    public void log(HttpServletRequest request) {
        String ipAddress;
        String forwardHeader= request.getHeader("X-Forwarded-For");
        if (forwardHeader == null) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = new StringTokenizer(forwardHeader, ",").nextToken().trim();
        }
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(logEmail);
        mailMessage.setSubject("New login");
        mailMessage.setText("A new login was detected from " + ipAddress);
        emailService.sendEmail(mailMessage);
    }

    public static String generateCommonLangPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    public GoogleIdToken verifyGoogleIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(this.googleId))
                .build();
        return verifier.verify(idTokenString);

    }
}
