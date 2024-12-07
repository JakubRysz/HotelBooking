package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.controller.mapper.BookingMapper;
import com.project.hotelBooking.controller.model.BookingCreateAdminDto;
import com.project.hotelBooking.controller.model.BookingCreateDto;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.controller.model.BookingEditDto;
import com.project.hotelBooking.service.*;
import com.project.hotelBooking.service.model.*;
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
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final ValidatorCustom validatorCustom;
    private final UserService userService;
    private final RoomService roomService;
    private final HotelService hotelService;
    private final LocalizationService localizationService;
    private final SimpleEmailService emailService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("admin/bookings")
    public BookingDto createBookingAdmin(@RequestBody BookingCreateAdminDto bookingDto) {
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        validatorCustom.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }


    @PostMapping("/bookings")
    public BookingDto createBookingUser(@RequestBody BookingCreateDto bookingDto) {
        Long authenticatedUserId = userService.getAuthenticatedUserId();
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        booking = booking.withUserId(authenticatedUserId);
        validatorCustom.validateBooking(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto createdBooking = bookingMapper.mapToBookingDto(bookingService.saveBooking(booking));
        emailService.sendMailCreatedBooking(bookingInfo);
        return createdBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin/bookings/{id}")
    public BookingDto getSingleBookingAdmin(@PathVariable Long id) throws ElementNotFoundException {
        return bookingMapper.mapToBookingDto(bookingService.getBookingById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin/bookings/user/{userId}")
    public List<BookingDto> getBookingsAdmin(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false)Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        //TODO: this is bad - refactor this, delegate to service layer
        return (bookingService.getBookingsByUserId(userId, page, sort).stream().map(bookingMapper::mapToBookingDto).collect(Collectors.toList()));
    }

    @GetMapping("/bookings")
    public List<BookingDto> getBookingsUser(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserServ user = userService.getUserByUsername(auth.getName());
        return (bookingService.getBookingsByUserId(user.getId(), page, sort).stream().map(bookingMapper::mapToBookingDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("admin/bookings")
    public BookingDto editBookingAdmin(@RequestBody BookingDto bookingDto) {
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        validatorCustom.validateBookingEditAdmin(booking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PutMapping("/bookings")
    public BookingDto editBookingUser(@RequestBody BookingEditDto bookingDto) {
        Long authenticatedUserId = userService.getAuthenticatedUserId();
        BookingServ booking = bookingMapper.mapToBooking(bookingDto);
        booking = booking.withUserId(authenticatedUserId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BookingServ oldBooking = bookingService.getBookingById(booking.getId());
        validatorCustom.validateBookingEditUser(booking, oldBooking);
        BookingInfo bookingInfo = getInfoFromBooking(booking);
        BookingDto editedBooking = bookingMapper.mapToBookingDto(bookingService.editBooking(booking));
        emailService.sendMailEditedBooking(bookingInfo);
        return editedBooking;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("admin/bookings/{id}")
    public void deleteBookingAdmin(@PathVariable Long id) {
        validatorCustom.validateIfBookingExistById(id);
        BookingServ bookingToDelete = bookingService.getBookingById(id);
        BookingInfo bookingInfo = getInfoFromBooking(bookingToDelete);
        bookingService.deleteBookingById(id);
        emailService.sendMailDeletedBooking(bookingInfo);
    }

    @DeleteMapping("/bookings/{id}")
    public void deleteBookingUser(@PathVariable Long id) {
        validatorCustom.validateIfBookingExistById(id);
        BookingServ bookingToDelete = bookingService.getBookingById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserServ user = userService.getUserByUsername(auth.getName());
        validatorCustom.validateIfUserIsOwnerOfBooking(bookingToDelete, user.getId());
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
