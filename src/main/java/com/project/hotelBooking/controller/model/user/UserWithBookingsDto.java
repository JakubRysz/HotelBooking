package com.project.hotelBooking.controller.model.user;

import com.project.hotelBooking.repository.model.Booking;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class UserWithBookingsDto extends UserDto {
    List<Booking> bookings;
}
