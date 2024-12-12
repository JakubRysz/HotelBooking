package com.project.hotelBooking.controller.model.localization;

import com.project.hotelBooking.controller.model.hotel.HotelDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LocalizationWithHotelsDto {
    Long id;
    String city;
    String country;
    List<HotelDto> hotels;
}
