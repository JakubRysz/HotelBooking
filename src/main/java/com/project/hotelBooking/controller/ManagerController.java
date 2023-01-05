package com.project.hotelBooking.controller;

import com.project.hotelBooking.service.Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ManagerController {

    private final Manager manager;

    @PostMapping("/initializeDb")
    public void initializeDatabase() {
        manager.initializeDb();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/clearDb")
    public void clearDatabase() {
        manager.clearDb();
    }
}
