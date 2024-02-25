package com.project.hotelBooking.service;

import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private static final int PAGE_SIZE=5;

    public Booking saveBooking(Booking Booking) {
        return bookingRepository.save(Booking);
    }
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    public void deleteBookingById(Long id) {
        bookingRepository.deleteById(id);
    }
    public List<Booking> getBookings(Integer page, Sort.Direction sort) {
        return bookingRepository.findAllBookings(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Booking> getBookingsByUserId(Long id, Integer page, Sort.Direction sort) {
        return bookingRepository.findAllByUserId(id, PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public Booking editBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
    public void deleteAllBookings() {
        bookingRepository.deleteAll();
    }
}
