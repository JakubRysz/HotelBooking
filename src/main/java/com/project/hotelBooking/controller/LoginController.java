package com.project.hotelBooking.controller;

import com.project.hotelBooking.config.LoginCredentials;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class LoginController {

    @PostMapping("/login")
    public void LoginUser(@RequestBody LoginCredentials loginCredentials){

    }
}
