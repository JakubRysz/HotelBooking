package com.project.hotelBooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class SimpleEmailServiceTest {

    @InjectMocks
    private SimpleEmailService simpleEmailService;
    @Mock
    private JavaMailSender javaMailSender;

    @Test
    public void shouldSendEmail() {
        //Given
        Mail mail = new Mail("kuba.rysz10@gmail.com", "Test", "Test message");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("kuba.rysz10@gmail.com");
        mailMessage.setSubject("Test");
        mailMessage.setText("Test message");
        //when
        simpleEmailService.send(mail);
        //Then
        verify(javaMailSender,times(1)).send(mailMessage);
    }
}