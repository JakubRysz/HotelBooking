package com.project.hotelBooking.security.controller;

import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.service.LostPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LostPasswordController {

    private final LostPasswordService lostPasswordService;

    @PostMapping("/lostPassword")
    public void lostPassword(@RequestBody String email) {
        lostPasswordService.sendEmailWithLink(email);
    }

    @PostMapping("/changePassword")
    public void changePassword(@RequestBody ChangedPassword changedPassword) {
        lostPasswordService.changePassword(changedPassword);
    }
}
