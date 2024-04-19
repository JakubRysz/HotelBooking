package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.controller.model.BookingWithoutUserDto;
import com.project.hotelBooking.service.model.BookingServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(BookingServ booking);
    BookingServ mapToBooking(BookingDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(BookingServ booking);
}
