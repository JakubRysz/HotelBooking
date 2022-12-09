package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.mapper.HotelMapper;
import com.project.hotelBooking.mapper.RoomMapper;
import com.project.hotelBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DbService {
    @Autowired
    private LocalizationRepository localizationRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private RoomMapper roomMapper;

    private static final int PAGE_SIZE=5;

    //Localization
    public Localization saveLocalization(Localization localization) {
        return localizationRepository.save(localization);
    }
    public Optional<Localization> getLocalizationById(Long id) {
        return localizationRepository.findById(id);
    }
    public void deleteLocalizationById(Long id) {
        localizationRepository.deleteById(id);
    }
    public List<Localization> getLocalizations(Integer page, Sort.Direction sort) {
        return localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Localization> getLocalizationsWithHotels(Integer page, Sort.Direction sort) {
        List<Localization> localizations =  localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = localizations.stream().map(Localization::getId).collect(Collectors.toList());
        List<HotelDto> hotelDtos = hotelRepository.findAllByLocalizationIdIn(ids).stream().map(hotel -> hotelMapper.mapToHotelDto(hotel)).toList();
        List<Hotel> hotels =hotelDtos.stream().map(hotelDto -> hotelMapper.mapToHotel(hotelDto)).collect(Collectors.toList());
        localizations.forEach(localization -> localization.setHotels(extractHotels(hotels, localization.getId())));
        return localizations;
    }
    private List<Hotel> extractHotels(List<Hotel> hotels, Long id) {
        return hotels.stream()
                .filter(hotel -> hotel.getLocalizationId()==id).collect(Collectors.toList());
    }
    public Localization editLocalization(Localization localization) {
        localizationRepository.findById(localization.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));
        return localizationRepository.save(localization);
    }
    public void deleteAllLocalizations() {
        localizationRepository.deleteAll();
    }

    //Hotel
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }
    public void deleteHotelById(Long id) {
        hotelRepository.deleteById(id);
    }
    public List<Hotel> getHotels(Integer page, Sort.Direction sort) {
        return hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Hotel> getHotelsWithRooms(Integer page, Sort.Direction sort) {
        List<Hotel> hotels =  hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = hotels.stream().map(Hotel::getId).collect(Collectors.toList());
        List<RoomDto> roomDtos = roomRepository.findAllByHotelIdIn(ids).stream().map(room -> roomMapper.mapToRoomDto(room)).toList();
        List<Room> rooms =roomDtos.stream().map(roomDto ->roomMapper.mapToRoom(roomDto)).collect(Collectors.toList());
        hotels.forEach(hotel -> hotel.setRooms(extractRooms(rooms, hotel.getId())));
        return hotels;
    }
    private List<Room> extractRooms(List<Room> rooms, Long id) {
        return rooms.stream()
                .filter(room -> room.getHotelId()==id).collect(Collectors.toList());
    }
    public Hotel editHotel(Hotel hotel) {
        hotelRepository.findById(hotel.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));
        return hotelRepository.save(hotel);
    }
    public void deleteAllHotels() {
        hotelRepository.deleteAll();
    }

    //Room
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }
    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }
    public List<Room> getRooms(Integer page, Sort.Direction sort) {
        return roomRepository.findAllRooms(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Room> getRoomsWithBookings(Integer page, Sort.Direction sort) {
        List<Room> rooms =  roomRepository.findAllRooms(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = rooms.stream().map(Room::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByRoomIdIn(ids);
        rooms.forEach(room -> room.setBookings(extractBookings(bookings, room.getId())));
        return rooms;
    }
    private List<Booking> extractBookings(List<Booking> bookings, Long id) {
        return bookings.stream()
                .filter(booking -> booking.getRoomId()==id).collect(Collectors.toList());
    }
    public Room editRoom(Room room) {
        roomRepository.findById(room.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));
        return roomRepository.save(room);
    }
    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }

    //User
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    public List<User> getUsers(Integer page, Sort.Direction sort) {
        return userRepository.findAllUsers(PageRequest.of(page,PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<User> getUsersWithBookings(Integer page, Sort.Direction sort) {
        List<User> users =  userRepository.findAllUsers(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = users.stream().map(User::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByUserIdIn(ids);
        users.forEach(user -> user.setBookings(extractBookingsUser(bookings, user.getId())));
        return users;
    }
    private List<Booking> extractBookingsUser(List<Booking> bookings, Long id) {
        return bookings.stream()
                .filter(booking -> booking.getUserId()==id).collect(Collectors.toList());
    }
    public User editUser(User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));
        return userRepository.save(user);
    }
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    //Booking
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
    public Booking editBooking(Booking booking) {
        bookingRepository.findById(booking.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));
        return bookingRepository.save(booking);
    }
    public void deleteAllBookings() {
        bookingRepository.deleteAll();
    }

}
