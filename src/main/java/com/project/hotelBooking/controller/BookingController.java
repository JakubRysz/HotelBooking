package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.controller.mapper.BookingMapper;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.service.*;
import com.project.hotelBooking.service.model.*;
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
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }

    @PostMapping("/bookings/own")
    public BookingDto createOwnBooking(@RequestBody BookingDto bookingDto) {
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/{id}")
    public BookingDto getSingleBooking(@PathVariable Long id) throws ElementNotFoundException {
        return bookingMapper.mapToBookingDto(bookingService.getBookingById(id));
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
        UserServ user = userService.getUserByUsername(auth.getName());
        return (bookingService.getBookingsByUserId(user.getId(), page, sort).stream().map(bookingMapper::mapToBookingDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings")
    public BookingDto editBooking(@RequestBody BookingDto bookingDto) {
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        BookingServ oldBooking = bookingService.getBookingById(booking.getId());
        validator.validateBookingEdit(booking, oldBooking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PutMapping("/bookings/own")
    public BookingDto editOwnBooking(@RequestBody BookingDto bookingDto) {
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserServ user = userService.getUserByUsername(auth.getName());
        BookingServ oldBooking = bookingService.getBookingById(booking.getId());
        validator.validateBookingEditUser(booking, oldBooking, user.getId());
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        BookingServ bookingToDelete = bookingService.getBookingById(id);
        BookingInfo bookingInfo = getInfoFromBooking(bookingToDelete);
        bookingService.deleteBookingById(id);
        emailService.sendMailDeletedBooking(bookingInfo);
    }

    @DeleteMapping("/bookings/own/{id}")
    public void deleteOwnBooking(@PathVariable Long id) {
        validator.validateIfBookingExistById(id);
        BookingServ bookingToDelete = bookingService.getBookingById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserServ user = userService.getUserByUsername(auth.getName());
        validator.validateIfUserIsOwnerOfBooking(bookingToDelete, user.getId());
        BookingInfo bookingInfo = getInfoFromBooking(bookingToDelete);
        bookingService.deleteBookingById(id);
        emailService.sendMailDeletedBooking(bookingInfo);
    }

    public BookingInfo getInfoFromBooking(BookingServ booking) {

        RoomServ room = roomService.getRoomById(booking.getRoomId());
        HotelServ hotel = hotelService.getHotelById(room.getHotelId());
        LocalizationServ localization = localizationService.getLocalizationById(hotel.getLocalizationId());
        UserServ user = userService.getUserById(booking.getUserId());

        return BookingInfo.builder()
                .booking(booking)
                .room(room)
                .hotel(hotel)
                .localization(localization)
                .user(user)
                .build();
    }
}
