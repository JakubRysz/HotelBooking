package com.project.hotelBooking.controller.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public class HotelBaseDto {
    String name;
    int numberOfStars;
    String hotelChain;
    Long localizationId;
}
