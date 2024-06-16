package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LocalizationWithHotelsDto {
    Long id;
    String city;
    String country;
    List<HotelDto> hotel;
}
