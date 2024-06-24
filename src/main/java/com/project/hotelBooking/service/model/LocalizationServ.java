package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@Builder
public class LocalizationServ {
    Long id;
    String city;
    String country;
    @With
    List<HotelServ> hotels;
}

