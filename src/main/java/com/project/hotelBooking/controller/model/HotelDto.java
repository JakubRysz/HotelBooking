package com.project.hotelBooking.controller.model;

import lombok.*;

@Value
@Builder
public class HotelDto {
        Long id;
        String name;
        int numberOfStars;
        String hotelChain;
        Long localizationId;
}

