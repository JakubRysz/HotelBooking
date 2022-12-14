package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LocalizationWithHotelsDto {
    private Long id;
    private String city;
    private String country;
    private List<HotelDto> hotel;
}
