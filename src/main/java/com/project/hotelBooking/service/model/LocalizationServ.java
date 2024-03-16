package com.project.hotelBooking.service.model;

import lombok.*;

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

