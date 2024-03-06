package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.model.BookingInfo;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimpleEmailService {

    private final JavaMailSender javaMailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMailMessage.class);

    public void send(final Mail mail) {
        try {
            SimpleMailMessage mailMessage = createSimpleMailMessage(mail);
            javaMailSender.send(mailMessage);
            LOGGER.info("Email has been sent");
        }
        catch (MailException e) {
            LOGGER.error("Failed to process email sending: ", e.getMessage(),e);
        }
    }

    private SimpleMailMessage createSimpleMailMessage(final Mail mail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail.getMailTo());
        mailMessage.setSubject(mail.getSubject());
        mailMessage.setText(mail.getMessage());
        return mailMessage;
    }

    public void sendMailCreatedUser(User user){
        Mail mail = Mail.builder()
                .mailTo(user.getEmail())
                .subject("Hotel booking - confirmation of creating a new user")
                .message("New user with data below has been created. \n\n"
                        + getUserInformation(user))
                .build();
        send(mail);
    }

    public void sendMailEditedUser(User user){
        Mail mail = Mail.builder()
                .mailTo(user.getEmail())
                .subject("Hotel booking - confirmation of editing a user")
                .message("User data after editing: \n\n"
                        + getUserInformation(user))
                .build();
        send(mail);
    }

    public void sendMailDeletedUser(User user){
        Mail mail = Mail.builder()
                .mailTo(user.getEmail())
                .subject("Hotel booking - confirmation of deleting a user")
                .message("User with data below has been deleted. \n\n"
                        + getUserInformation(user))
                .build();
        send(mail);
    }


    public void sendMailCreatedBooking(BookingInfo bookingInfo) {
        Mail mail = Mail.builder()
                .mailTo(bookingInfo.getUser().getEmail())
                .subject("Hotel booking - confirmation of creating a new booking")
                .message("New booking with data below has been created. \n\n"
                        + getBookingMainInformation(bookingInfo))
                .build();
        send(mail);
    }

    public void sendMailEditedBooking(BookingInfo bookingInfo) {
        Mail mail = Mail.builder()
                .mailTo(bookingInfo.getUser().getEmail())
                .subject("Hotel booking - confirmation of editing a booking")
                .message("Booking data after editing: \n\n"
                        + getBookingMainInformation(bookingInfo))
                .build();
        send(mail);
    }

    public void sendMailDeletedBooking(BookingInfo bookingInfo) {
        Mail mail = Mail.builder()
                .mailTo(bookingInfo.getUser().getEmail())
                .subject("Hotel booking - confirmation of deleting a booking")
                .message("Booking with data below has been deleted. \n\n"
                        + getBookingMainInformation(bookingInfo))
                .build();
        send(mail);
    }

    private String getUserInformation(User user) {
        String userInformation =
                        "First name: "+user.getFirstName()+"\n"
                        + "Last name: "+user.getLastName()+"\n"
                        + "Date of birth: "+user.getDateOfBirth()+"\n"
                        + "Username: "+user.getUsername()+"\n"
                        + "Role: "+user.getRole()+"\n"
                        + "E-mail: "+user.getEmail()+"\n";
        return userInformation;
    }

    private String getBookingMainInformation(BookingInfo bookingInfo) {
        String bookingInformation =
                        "Start date: " + bookingInfo.getBooking().getStart_date()+"\n"
                        + "End date: " + bookingInfo.getBooking().getEnd_date()+"\n"
                        + "Booking owner: "+bookingInfo.getUser().getFirstName()+" "+bookingInfo.getUser().getLastName()+"\n"
                        + "Hotel name: "+ bookingInfo.getHotel().getName()+"\n"
                        + "Hotel chain: "+ bookingInfo.getHotel().getHotelChain()+"\n"
                        + "Localization: "+ bookingInfo.getLocalization().getCountry()+", "+bookingInfo.getLocalization().getCity()+"\n"
                        + "Room number: " + bookingInfo.getRoom().getRoomNumber()+"\n"
                        + "Room standard: " + bookingInfo.getRoom().getStandard()+"\n"
                        + "Maximum number of persons in room: "+ bookingInfo.getRoom().getNumberOfPersons()+"\n";
        return bookingInformation;
    }
}
