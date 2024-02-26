package com.project.hotelBooking.domain;

import com.project.hotelBooking.repository.model.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInfo {
    private Booking booking;
    private Localization localization;
    private Hotel hotel;
    private Room room;
    private User user;
}
