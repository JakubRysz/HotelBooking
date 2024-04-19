package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookingInfo {
    BookingServ booking;
    LocalizationServ localization;
    HotelServ hotel;
    RoomServ room;
    UserServ user;
}
