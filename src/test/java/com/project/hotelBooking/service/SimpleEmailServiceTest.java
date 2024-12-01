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
        LocalDate bookingStartDate = LocalDate.now().plusDays(10);
        LocalDate bookingEndDate = LocalDate.now().plusDays(2);
        String userName = "Jan";
        String userLastName = "Kowalski";
        String hotelName = "hotel1";
        String hotelChain = "Marriott";
        String country = "Poland";
        String city = "Cracow";
        int roomNumber = 2;
        int roomStandard = 2;
        int numberOfPersons = 3;

        String mailMessageSubject = "Hotel booking - confirmation of creating a new booking";
        String mailMessageText = "New booking with data below has been created. \n\n"
                + "Start date: " + bookingStartDate + "\n"
                + "End date: " + bookingEndDate + "\n"
                + "Booking owner: " + userName + " " + userLastName + "\n"
                + "Hotel name: " + hotelName + "\n"
                + "Hotel chain: " + hotelChain + "\n"
                + "Localization: " + country + ", " + city + "\n"
                + "Room number: " + roomNumber + "\n"
                + "Room standard: " + roomStandard + "\n"
                + "Maximum number of persons in room: " + numberOfPersons + "\n";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(EMAIL_TEST);
        mailMessage.setSubject(mailMessageSubject);
        mailMessage.setText(mailMessageText);

        LocalizationServ localization = LocalizationServ.builder()
                .id(1L)
                .city(city)
                .country("Poland")
                .hotels(null)
                .build();

        HotelServ hotel = HotelServ.builder()
                .id(1L)
                .name(hotelName)
                .numberOfStars(2)
                .hotelChain(hotelChain)
                .localizationId(1L)
                .rooms(null)
                .build();

        RoomServ room = RoomServ.builder()
                .id(1L)
                .roomNumber(roomNumber)
                .numberOfPersons(numberOfPersons)
                .standard(roomStandard)
                .hotelId(1L)
                .bookings(null)
                .build();

        UserServ user = UserServ.builder()
                .firstName(userName)
                .lastName(userLastName)
                .dateOfBirth(LocalDate.now().minusYears(30))
                .username("jankowalski")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        BookingServ booking = BookingServ.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .startDate(bookingStartDate)
                .endDate(bookingEndDate)
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