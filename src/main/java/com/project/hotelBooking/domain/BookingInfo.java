package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfo {
    private Booking booking;
    private Localization localization;
    private Hotel hotel;
    private Room room;
    private User user;
}
