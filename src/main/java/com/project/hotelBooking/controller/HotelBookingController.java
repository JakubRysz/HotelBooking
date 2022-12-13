package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.mapper.*;

import com.project.hotelBooking.service.DbService;
import com.project.hotelBooking.service.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
public class HotelBookingController {

    @Autowired
    private DbService dbService;
    @Autowired
    private LocalizationMapper localizationMapper;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private Manager manager;
    @Autowired
    private Validator validator;

    //Localization
    @PostMapping("/localizations")
    public LocalizationDto createLocalization(@RequestBody LocalizationDto localizationDto) {
        Localization localization = localizationMapper.mapToLocalization(localizationDto);
        validator.validateLocalization(localization);
        return localizationMapper.mapToLocalizationDto(dbService.saveLocalization(localizationMapper.mapToLocalization(localizationDto)));
    }
    @GetMapping("/localizations/Hotels/{id}")
    public LocalizationWithHotelsDto getSingleLocalizationWithHotels(@PathVariable Long id) {
        return localizationMapper.mapToLocalizationWithHotelsDto(dbService.getLocalizationById(id)
                .orElseThrow(()->new ElementNotFoundException("No such localization")));
    }
    @GetMapping("/localizations/Hotels")
    public List<LocalizationWithHotelsDto> getLocalizationsWithHotels(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getLocalizationsWithHotels(page, sort).stream()
                .map(k->localizationMapper.mapToLocalizationWithHotelsDto(k))
                .collect(Collectors.toList()));
    }
    @GetMapping("/localizations")
    public List<LocalizationDto> getLocalizations(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getLocalizations(page, sort).stream()
                .map(k->localizationMapper.mapToLocalizationDto(k))
                .collect(Collectors.toList()));
    }
    @PutMapping("/localizations")
    public LocalizationDto editLocalization(@RequestBody LocalizationDto localizationDto) {
        Localization localization = localizationMapper.mapToLocalization(localizationDto);
        validator.validateLocalizationEdit(localization);
        return localizationMapper.mapToLocalizationDto(dbService.editLocalization(localization));
    }
    @DeleteMapping("/localizations/{id}")
    public void deleteSingleLocalization(@PathVariable Long id) {
        dbService.deleteLocalizationById(id);
    }

    //Hotel
    @PostMapping("/hotels")
    public HotelDto createHotel(@RequestBody HotelWithRoomsDto hotelWithRoomsDto) {
        Hotel hotel = hotelMapper.mapToHotel(hotelWithRoomsDto);
        validator.validateHotel(hotel);
        return hotelMapper.mapToHotelDto(dbService.saveHotel(hotel));
    }
    @GetMapping("/hotels/Rooms/{id}")
    public HotelWithRoomsDto getSingleHotelWithRooms(@PathVariable Long id) {
        return hotelMapper.mapToHotelWithRoomsDto(dbService.getHotelById(id)
                .orElseThrow(()->new ElementNotFoundException("No such hotel")));
    }
    @GetMapping("/hotels/Rooms")
    public List<HotelWithRoomsDto> getHotelsWithRooms(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getHotelsWithRooms(page, sort)
                .stream().map(k -> hotelMapper.mapToHotelWithRoomsDto(k))
                .collect(Collectors.toList()));
    }
    @GetMapping("/hotels")
    public List<HotelDto> getHotels(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getHotels(page, sort).stream()
                .map(k -> hotelMapper.mapToHotelDto(k))
                .collect(Collectors.toList()));
    }
    @PutMapping("/hotels")
    public HotelDto editHotel(@RequestBody HotelDto hotelDto) {
        Hotel hotel = hotelMapper.mapToHotel(hotelDto);
        validator.validateHotelEdit(hotel);
        return hotelMapper.mapToHotelDto(dbService.editHotel(hotel));
    }
    @DeleteMapping("/hotels/{id}")
    public void deleteHotel(@PathVariable Long id) {
        dbService.deleteHotelById(id);
    }

    //Room
    @PostMapping("/rooms")
    public RoomDto createRoom(@RequestBody RoomWithBookingsDto roomWithBookingsDto) {
        Room room = roomMapper.mapToRoom(roomWithBookingsDto);
        validator.validateRoom(room);
        return roomMapper.mapToRoomDto(dbService.saveRoom(room));
    }
    @GetMapping("/rooms/Bookings/{id}")
    public RoomWithBookingsDto getSingleRoom(@PathVariable Long id) {
        return roomMapper.mapToRoomWithBookingsDto(dbService.getRoomById(id)
                .orElseThrow(()->new ElementNotFoundException("No such room")));
    }
    @GetMapping("/rooms/Bookings")
    public List<RoomWithBookingsDto> getRoomsWithBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getRoomsWithBookings(page, sort).stream()
                .map(k -> roomMapper.mapToRoomWithBookingsDto(k))
                .collect(Collectors.toList()));
    }
    @GetMapping("/rooms")
    public List<RoomDto> getRooms(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getRooms(page, sort).stream().map(k -> roomMapper.mapToRoomDto(k)).collect(Collectors.toList()));
    }
    @PutMapping("/rooms")
    public RoomDto editRoom(@RequestBody RoomDto roomDto) {
        Room room = roomMapper.mapToRoom(roomDto);
        validator.validateRoomEdit(room);
        return roomMapper.mapToRoomDto(dbService.editRoom(room));
    }
    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        dbService.deleteRoomById(id);
    }

    //User
    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        validator.validateUser(user);
        return userMapper.mapToUserDto(dbService.saveUser(userMapper.mapToUser(userDto)));
    };

    @GetMapping("/users/Bookings/{id}")
    public UserWithBookingDto getSingleUser(@PathVariable Long id) {
        return userMapper.mapToUserWithBookingDto(dbService.getUserById(id)
                .orElseThrow(()->new ElementNotFoundException("No such user")));
    }
    @GetMapping("/users/Bookings")
    public List<UserWithBookingDto> getUsersWithBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getUsersWithBookings(page, sort)
                .stream().map(k -> userMapper.mapToUserWithBookingDto(k))
                .collect(Collectors.toList()));
    }
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getUsers(page, sort)
                .stream().map(k -> userMapper.mapToUserDto(k))
                .collect(Collectors.toList()));
    }
    @PutMapping("/users")
    public UserDto editUser(@RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        validator.validateUserEdit(user);
        return userMapper.mapToUserDto(dbService.editUser(user));
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        dbService.deleteUserById(id);
    }

    //Booking
    @PostMapping("/bookings")
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBooking(booking);
        return bookingMapper.mapToBookingDto(dbService.saveBooking(bookingMapper.mapToBooking(bookingDto)));
    }

    @GetMapping("/bookings/{id}")
    public BookingDto getSingleBooking(@PathVariable Long id) throws ElementNotFoundException {
        return bookingMapper.mapToBookingDto(dbService.getBookingById(id)
                .orElseThrow(()->new ElementNotFoundException("No such booking")));
    }
    @GetMapping("/bookings")
    public List<BookingDto> getBookings(@RequestParam (required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (dbService.getBookings(page, sort).stream().map(k -> bookingMapper.mapToBookingDto(k)).collect(Collectors.toList()));
    }
    @PutMapping("/bookings")
    public BookingDto editBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        validator.validateBookingEdit(booking);
        return bookingMapper.mapToBookingDto(dbService.editBooking(booking));
    }
    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        dbService.deleteBookingById(id);
    }

    //Others
    @PostMapping("/initializeDb")
    public void initializeDatabase() {
        manager.initializeDb();
    }
    @DeleteMapping("/clearDb")
    public void clearDatabase() {
        manager.clearDb();
    }

}
