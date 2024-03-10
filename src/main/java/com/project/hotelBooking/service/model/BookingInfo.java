package com.project.hotelBooking.service.model;

import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.repository.model.Room;
import com.project.hotelBooking.repository.model.User;
import lombok.*;

@Value
@Builder
public class BookingInfo {
    BookingServ booking;
    Localization localization;
    HotelServ hotel;
    Room room;
    User user;
}
