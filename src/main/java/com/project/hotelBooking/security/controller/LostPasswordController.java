package com.project.hotelBooking.security.controller;

import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.model.EmailDto;
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
    public void lostPassword(@RequestBody EmailDto emailDto) {
        lostPasswordService.sendEmailWithLink(emailDto);
    }

    @PostMapping("/changePassword")
    public void changePassword(@RequestBody ChangedPassword changedPassword) {
        lostPasswordService.changePassword(changedPassword);
    }
}
