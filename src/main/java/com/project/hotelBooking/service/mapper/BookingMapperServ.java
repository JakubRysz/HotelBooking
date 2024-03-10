package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.repository.model.Booking;
import com.project.hotelBooking.service.model.BookingServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapperServ {

    BookingServ mapToServiceBooking(Booking booking);
    List<BookingServ> mapToServiceBookings(List<Booking> bookings);
    Booking mapToRepositoryBooking(BookingServ booking);
}
