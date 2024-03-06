package com.project.hotelBooking.service.model;

import com.project.hotelBooking.repository.model.*;
import lombok.*;

@Value
@Builder
public class BookingInfo {
    Booking booking;
    Localization localization;
    Hotel hotel;
    Room room;
    User user;
}
