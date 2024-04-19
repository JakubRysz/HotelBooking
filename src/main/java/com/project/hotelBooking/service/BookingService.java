package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.BookingRepository;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.model.BookingServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private static final int PAGE_SIZE = 5;

    @Autowired
    BookingMapperServ bookingMapper;

    public BookingServ saveBooking(BookingServ booking) {
        return bookingMapper.mapToBooking(
        bookingRepository.save(bookingMapper.mapToRepositoryBooking(booking))
        );
    }

    public BookingServ getBookingById(Long id) {
        return bookingMapper.mapToBooking(bookingRepository.findById(id).orElseThrow(()
                -> new ElementNotFoundException("No such booking")));
    }

    public void deleteBookingById(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<BookingServ> getBookings(Integer page, Sort.Direction sort) {
        return bookingMapper.mapToBookings(
                bookingRepository.findAllBookings(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }

    public List<BookingServ> getBookingsByUserId(Long id, Integer page, Sort.Direction sort) {
        return bookingMapper.mapToBookings(
                bookingRepository.findAllByUserId(id, PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }

    public BookingServ editBooking(BookingServ bookingServ) {
        return bookingMapper.mapToBooking(
                        bookingRepository.save(bookingMapper.mapToRepositoryBooking(bookingServ))
                );
    }

    public void deleteAllBookings() {
        bookingRepository.deleteAll();
    }
}
