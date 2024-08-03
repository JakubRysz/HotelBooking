package com.project.hotelBooking.service;

import com.project.hotelBooking.service.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static com.project.hotelBooking.common.CommonTestConstants.ROLE_USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
                + "Localization: Poland, Cracow\n"
                + "Room number: 2\n"
                + "Room standard: 2\n"
                + "Maximum number of persons in room: 3\n");

        LocalizationServ localization = LocalizationServ.builder()
                .id(1L)
                .city("Cracow")
                .country("Poland")
                .hotels(null)
                .build();

        HotelServ hotel = HotelServ.builder()
                .id(1L)
                .name("hotel1")
                .numberOfStars(2)
                .hotelChain("Mariot")
                .localizationId(1L)
                .rooms(null)
                .build();

        RoomServ room = RoomServ.builder()
                .id(1L)
                .roomNumber(2)
                .numberOfPersons(3)
                .standard(2)
                .hotelId(1L)
                .bookings(null)
                .build();

        UserServ user = UserServ.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        BookingServ booking = BookingServ.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .startDate(LocalDate.of(2023, 02, 17))
                .endDate(LocalDate.of(2023, 02, 21))
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