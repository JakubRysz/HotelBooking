package com.project.hotelBooking.security.controller;

import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.model.EmailDto;
import com.project.hotelBooking.security.service.LostPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class LostPasswordController {

    private final LostPasswordService lostPasswordService;

    @PostMapping("/lostPassword")
    public void lostPassword(@RequestBody EmailDto emailDto) {
        lostPasswordService.sendEmailWithLink(emailDto);
    }

    @PostMapping("/changePassword")
    public void changePassword(@Valid @RequestBody ChangedPassword changedPassword) {
        lostPasswordService.changePassword(changedPassword);
    }
}
