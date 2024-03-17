package com.project.hotelBooking.service.model;

import lombok.*;

@Value
@Builder
public class Mail {
    String mailTo;
    String subject;
    String message;
}
