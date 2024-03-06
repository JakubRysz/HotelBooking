package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Booking;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.controller.model.BookingWithoutUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(Booking booking);
    Booking mapToBooking(BookingDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(Booking booking);
}
