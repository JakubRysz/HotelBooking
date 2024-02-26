package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.BookingDto;
import com.project.hotelBooking.domain.BookingWithoutUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(Booking booking);
    Booking mapToBooking(BookingDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(Booking booking);
}
