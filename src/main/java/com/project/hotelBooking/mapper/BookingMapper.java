package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Booking;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.controller.model.BookingWithoutUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(BookingServ booking);
    BookingServ mapToBooking(BookingDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(BookingServ booking);
}
