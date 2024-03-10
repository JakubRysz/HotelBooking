package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.model.BookingInfo;
//import com.project.hotelBooking.service.model.Mail;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.HotelServ;
import com.project.hotelBooking.service.model.Mail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SimpleEmailServiceTest {

    @InjectMocks
    private SimpleEmailService simpleEmailService;
    @Mock
    private JavaMailSender javaMailSender;
    String EMAIL_TEST="application.test1010@gmail.com";

    @Test
    public void shouldSendEmail() {
        //Given
        Mail mail = Mail.builder()
                .mailTo(EMAIL_TEST)
                .subject("Test")
                .message("Test message")
                .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(EMAIL_TEST);
        mailMessage.setSubject("Test");
        mailMessage.setText("Test message");
        //when
        simpleEmailService.send(mail);
        //Then
        verify(javaMailSender,times(1)).send(mailMessage);
    }

    @Test
    public void shouldSendEmailCreatedBooking() {
        //Given

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(EMAIL_TEST);
        mailMessage.setSubject("Hotel booking - confirmation of creating a new booking");
        mailMessage.setText("New booking with data below has been created. \n\n"
        +"Start date: 2023-02-17\n"
                + "End date: 2023-02-21\n"
                + "Booking owner: Jan Kowalski\n"
                + "Hotel name: hotel1\n"
                + "Hotel chain: Mariot\n"
                + "Localization: Poland, Krakow\n"
                + "Room number: 2\n"
                + "Room standard: 2\n"
                + "Maximum number of persons in room: 3\n");

        Localization localization = new Localization(null, "Krakow", "Poland",null);
        HotelServ hotel = HotelServ.builder()
                .id(1L)
                .name("hotel1")
                .numberOfStars(2)
                .hotelChain("Mariot")
                .localizationId(1L)
                .rooms(null)
                .build();

        Room room = new Room(1L, 2, 3, 2, 1L,null);
        User user = new User(1L, "Jan", "Kowalski", LocalDate.of(1979, 1, 10),"jankowalski","jankowalski123","ROLE_USER", EMAIL_TEST, null);
        BookingServ booking = BookingServ.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .start_date(LocalDate.of(2023, 02, 17))
                .end_date(LocalDate.of(2023, 02, 21))
                .build();

        BookingInfo bookingInfo = BookingInfo.builder()
                .booking(booking)
                .room(room)
                .hotel(hotel)
                .localization(localization)
                .user(user)
                .build();

        //when
        simpleEmailService.sendMailCreatedBooking(bookingInfo);
        //Then
        verify(javaMailSender,times(1)).send(mailMessage);
    }

}