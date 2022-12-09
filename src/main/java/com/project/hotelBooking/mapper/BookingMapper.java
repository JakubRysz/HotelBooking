package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.BookingDto;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getStart_date(),
                booking.getEnd_date()

        );
    }
    public Booking mapToBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getUserId(),
                bookingDto.getRoomId(),
                bookingDto.getStart_date(),
                bookingDto.getEnd_date()

        );
    }
}
