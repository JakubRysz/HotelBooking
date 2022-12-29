package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.BookingDto;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.mapper.BookingMapper;
import com.project.hotelBooking.service.BookingService;
import com.project.hotelBooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bookings")
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        return bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
    }
    @PostMapping("/bookings/own")
    public BookingDto createOwnBooking(@RequestBody BookingDto bookingDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        bookingDto.setUserId(user.getId());
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        return bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/{id}")
    public BookingDto getSingleBooking(@PathVariable Long id) throws ElementNotFoundException {
        return bookingMapper.mapToBookingDto(bookingService.getBookingById(id)
                .orElseThrow(()->new ElementNotFoundException("No such booking")));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<BookingDto> getBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (bookingService.getBookings(page, sort).stream().map(k -> bookingMapper.mapToBookingDto(k)).collect(Collectors.toList()));
    }
    @GetMapping("/bookings/own")
    public List<BookingDto> getOwnBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return (bookingService.getBookingsByUserId(user.getId(),page, sort).stream().map(k -> bookingMapper.mapToBookingDto(k)).collect(Collectors.toList()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings")
    public BookingDto editBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        Booking oldBooking = bookingService.getBookingById(booking.getId())
                .orElseThrow(() -> new ElementNotFoundException("No such booking"));
        validator.validateBookingEdit(booking, oldBooking);
        booking.setId(oldBooking.getId());
        return bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
    }

    @PutMapping("/bookings/own")
    public BookingDto editOwnBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        Booking oldBooking = bookingService.getBookingById(booking.getId())
                .orElseThrow(() -> new ElementNotFoundException("No such booking"));
        validator.validateBookingEditUser(booking, oldBooking, user.getId());
        booking.setId(oldBooking.getId());
        return bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        bookingService.deleteBookingById(id);
    }
    @DeleteMapping("/bookings/own/{id}")
    public void deleteOwnBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(()->new ElementNotFoundException("No such booking"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        validator.validateIfUserIsOwnerOfBooking(booking, user.getId());
        bookingService.deleteBookingById(id);
    }
}
