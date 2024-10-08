package com.project.hotelBooking.controller.model;

import lombok.*;

@Value
@Builder
public class LocalizationDto {
    Long id;
    String city;
    String country;
}
