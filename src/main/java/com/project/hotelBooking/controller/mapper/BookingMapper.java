package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.booking.*;
import com.project.hotelBooking.service.model.BookingServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(BookingServ booking);
    BookingServ mapToBooking(BookingDto bookingDto);
    BookingServ mapToBooking(BookingCreateAdminDto bookingDto);
    BookingServ mapToBooking(BookingCreateDto bookingDto);
    BookingServ mapToBooking(BookingEditDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(BookingServ booking);
}
