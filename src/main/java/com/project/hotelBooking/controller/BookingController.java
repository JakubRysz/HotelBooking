package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.mapper.BookingMapper;
import com.project.hotelBooking.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final Validator validator;
    private final UserService userService;
    private final RoomService roomService;
    private final HotelService hotelService;
    private final LocalizationService localizationService;
    private final SimpleEmailService emailService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bookings")
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }

    @PostMapping("/bookings/own")
    public BookingDto createOwnBooking(@RequestBody BookingDto bookingDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName()).orElseThrow(
                () -> new ElementNotFoundException("No such user"));
        bookingDto.setUserId(user.getId());
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/{id}")
    public BookingDto getSingleBooking(@PathVariable Long id) throws ElementNotFoundException {
        return bookingMapper.mapToBookingDto(bookingService.getBookingById(id)
                .orElseThrow(() -> new ElementNotFoundException("No such booking")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<BookingDto> getBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (bookingService.getBookings(page, sort).stream().map(bookingMapper::mapToBookingDto).collect(Collectors.toList()));
    }

    @GetMapping("/bookings/own")
    public List<BookingDto> getOwnBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName()).orElseThrow(
                () -> new ElementNotFoundException("No such user"));
        return (bookingService.getBookingsByUserId(user.getId(), page, sort).stream().map(bookingMapper::mapToBookingDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings")
    public BookingDto editBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        Booking oldBooking = bookingService.getBookingById(booking.getId())
                .orElseThrow(() -> new ElementNotFoundException("No such booking"));
        validator.validateBookingEdit(booking, oldBooking);
        booking.setId(oldBooking.getId());
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PutMapping("/bookings/own")
    public BookingDto editOwnBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName()).orElseThrow(
                () -> new ElementNotFoundException("No such user"));
        Booking oldBooking = bookingService.getBookingById(booking.getId())
                .orElseThrow(() -> new ElementNotFoundException("No such booking"));
        validator.validateBookingEditUser(booking, oldBooking, user.getId());
        booking.setId(oldBooking.getId());
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        Booking bookingToDelete = bookingService.getBookingById(id).orElseThrow(() -> new ElementNotFoundException("No such booking"));
        BookingInfo bookingInfo = getInfoFromBooking(bookingToDelete);
        bookingService.deleteBookingById(id);
        emailService.sendMailDeletedBooking(bookingInfo);
    }

    @DeleteMapping("/bookings/own/{id}")
    public void deleteOwnBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        Booking bookingToDelete = bookingService.getBookingById(id).orElseThrow(() -> new ElementNotFoundException("No such booking"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName()).orElseThrow(
                () -> new ElementNotFoundException("No such user"));
        validator.validateIfUserIsOwnerOfBooking(bookingToDelete, user.getId());
        BookingInfo bookingInfo = getInfoFromBooking(bookingToDelete);
        bookingService.deleteBookingById(id);
        emailService.sendMailDeletedBooking(bookingInfo);
    }

    public BookingInfo getInfoFromBooking(Booking booking) {
        BookingInfo bookingInfo = new BookingInfo();
        bookingInfo.setBooking(booking);
        bookingInfo.setRoom(roomService.getRoomById(booking.getRoomId()).orElseThrow(() -> new ElementNotFoundException("No such room")));
        bookingInfo.setHotel(hotelService.getHotelById(bookingInfo.getRoom().getHotelId()).orElseThrow(() -> new ElementNotFoundException("No such hotel")));
        bookingInfo.setLocalization(localizationService.getLocalizationById(bookingInfo.getHotel().getLocalizationId()).orElseThrow(() -> new ElementNotFoundException("No such Localization")));
        bookingInfo.setUser(userService.getUserById(booking.getUserId()).orElseThrow(() -> new ElementNotFoundException("No such user")));
        return bookingInfo;
    }
}
