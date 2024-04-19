package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Mail {
    String mailTo;
    String subject;
    String message;
}
