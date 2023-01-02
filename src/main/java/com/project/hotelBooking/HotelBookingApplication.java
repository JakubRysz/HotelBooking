package com.project.hotelBooking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class HotelBookingApplication {

    public static void main(String[] args) {

        SpringApplication.run(HotelBookingApplication.class, args);

    }
}
