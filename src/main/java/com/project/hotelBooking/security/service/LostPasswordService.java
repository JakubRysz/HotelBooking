package com.project.hotelBooking.security.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.security.model.ChangePassword;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LostPasswordService {


    private final UserRepository userRepository;
    private final SimpleEmailService emailService;
    @Value("${app.serviceAddress}")
    private String serviceAddress;

    public void sendEmailWithLink(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ElementNotFoundException("There is no user with given email"));

        String hash = generateHashForLostPassword(user);
        user.setHash(hash);
        user.setHashDate(LocalDateTime.now());
        String MAIL_SUBJECT_RESET_PASSWORD = "Hotel booking - reset password request";
        Mail mail = Mail.builder()
                .mailTo(email)
                .subject(MAIL_SUBJECT_RESET_PASSWORD)
                .message(createMailMessage(hash))
                .build();
        emailService.send(mail);
    }

    private String createMailMessage(String hashLink) {
        return "Please click to link bellow to reset your password:" +
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

    //TODO: implement LostPasswordService
    public void changePassword(ChangePassword changePassword) {

    }
}
