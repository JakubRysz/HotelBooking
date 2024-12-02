package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.BookingCreateAdminDto;
import com.project.hotelBooking.controller.model.BookingCreateDto;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.controller.model.BookingWithoutUserDto;
import com.project.hotelBooking.service.model.BookingServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto mapToBookingDto(BookingServ booking);
    BookingServ mapToBooking(BookingDto bookingDto);
    BookingServ mapToBooking(BookingCreateAdminDto bookingDto);
    BookingServ mapToBooking(BookingCreateDto bookingDto);
    BookingWithoutUserDto mapToBookingWithoutUserDto(BookingServ booking);
}
