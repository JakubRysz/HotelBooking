package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.repository.model.Booking;
import com.project.hotelBooking.service.model.BookingServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapperServ {

    BookingServ mapToBooking(Booking booking);
    List<BookingServ> mapToBookings(List<Booking> bookings);
    Booking mapToRepositoryBooking(BookingServ booking);
}
