package com.project.hotelBooking.controller;

import com.project.hotelBooking.service.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
public class ManagerController {

    @Autowired
    private Manager manager;

    @PostMapping("/initializeDb")
    public void initializeDatabase() {
        manager.initializeDb();
    }
    @DeleteMapping("/clearDb")
    public void clearDatabase() {
        manager.clearDb();
    }
}
