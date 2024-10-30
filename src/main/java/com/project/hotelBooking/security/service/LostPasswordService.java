package com.project.hotelBooking.security.service;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.security.exceptions.PasswordMismatchException;
import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.model.EmailDto;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LostPasswordService {


    private final UserRepository userRepository;
    private final SimpleEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.serviceAddress}")
    private String serviceAddress;

    @Value("${app.passwordResetLinkValidityMinutes}")
    private int passwordResetLinkValidityMinutes;

    public void sendEmailWithLink(EmailDto emailDto) {
        User user = userRepository.findTopByEmail(emailDto.getEmail())
                .orElseThrow(() -> new ElementNotFoundException("There is no user with given email"));

        String hash = generateHashForLostPassword(user);
        user.setHash(hash);
        user.setHashDate(LocalDateTime.now());
        String MAIL_SUBJECT_RESET_PASSWORD = "Hotel booking - reset password request";
        Mail mail = Mail.builder()
                .mailTo(emailDto.getEmail())
                .subject(MAIL_SUBJECT_RESET_PASSWORD)
                .message(createMailMessage(hash))
                .build();
        emailService.send(mail);
    }

    private String createMailMessage(String hashLink) {
        return "Please click link below to reset your password:" +
                "\n\n" + createLink(hashLink) +
                "\n\nThank you";
    }

    private String createLink(String hash) {
        return serviceAddress + "/lostPassword/" + hash;
    }

    private String generateHashForLostPassword(User user) {
        String toHash = user.getId() + user.getUsername() + user.getPassword() + LocalDateTime.now();
        return DigestUtils.sha256Hex(toHash);
    }

    public void changePassword(ChangedPassword changedPassword) {
        checkIfGivenPasswordsMatch(changedPassword);
        User user = userRepository.findByHash(changedPassword.getHash())
                .orElseThrow(() -> new BadRequestException("Invalid link to change password"));
        if(checkIfChangePasswordLinkIsNotExpired(user)) {
            updateUserPassword(changedPassword, user);
        } else {
            throw new RuntimeException("Link to change password is expired");
        }
    }

    private void updateUserPassword(ChangedPassword changedPassword, User user) {
        user.setPassword(passwordEncoder.encode(changedPassword.getPassword()));
        user.setHash(null);
        user.setHashDate(null);
    }

    private boolean checkIfChangePasswordLinkIsNotExpired(User user) {
        return user.getHashDate().plusMinutes(passwordResetLinkValidityMinutes).isAfter(LocalDateTime.now());
    }

    private static void checkIfGivenPasswordsMatch(ChangedPassword changedPassword) {
        if(!Objects.equals(changedPassword.getPassword(), changedPassword.getRepeatPassword())) {
            throw new PasswordMismatchException("Given passwords must be the same");
        }
    }
}
