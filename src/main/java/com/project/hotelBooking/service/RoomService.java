package com.project.hotelBooking.service;

import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.Room;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private static final int PAGE_SIZE=5;

    public Room getRoomByRoomNumberAndHotelId(Room room) { return roomRepository.findRoomByRoomNumberAndHotelId(
            room.getRoomNumber(),room.getHotelId());}
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
        return roomRepository.save(room);
    }
    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }
}
