package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.BookingRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.mapper.RoomMapperServ;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.RoomServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final RoomMapperServ roomMapperServ;
    private final BookingMapperServ bookingMapperServ;
    private static final int PAGE_SIZE = 5;

    public RoomServ getRoomById(Long id) {

        return roomMapperServ.mapToRoom(roomRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("No such room"))
        );
    }

    public RoomServ getRoomByRoomNumberAndHotelId(RoomServ room) {
        return roomMapperServ.mapToRoom(
                roomRepository.findRoomByRoomNumberAndHotelId(room.getRoomNumber(), room.getHotelId())
                        .orElseThrow(() -> new ElementNotFoundException("No such room"))
        );
    }

    public RoomServ saveRoom(RoomServ room) {

        return roomMapperServ.mapToRoom(
                roomRepository.save(roomMapperServ.mapToRepositoryRoom(room))
        );
    }

    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

    public List<RoomServ> getRooms(Integer page, Sort.Direction sort) {
        return roomMapperServ.mapToRoms(
                roomRepository.findAllRooms(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }

    public List<RoomServ> getRoomsWithBookings(Integer page, Sort.Direction sort) {
        List<RoomServ> rooms = roomMapperServ.mapToRoms(
                roomRepository.findAllRooms(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
        List<Long> roomsIds = rooms.stream()
                .map(RoomServ::getId)
                .collect(Collectors.toList());
        List<BookingServ> bookings = bookingMapperServ.mapToBookings(
                bookingRepository.findAllByRoomIdIn(roomsIds)
        );
        rooms.forEach(room -> room.withBookings(extractBookings(bookings, room.getId())));
        return rooms;
    }

    private List<BookingServ> extractBookings(List<BookingServ> bookings, Long id) {
        return bookings.stream()
                .filter(booking -> Objects.equals(booking.getRoomId(), id)).collect(Collectors.toList());
    }

    public RoomServ editRoom(RoomServ room) {

        return roomMapperServ.mapToRoom(
                roomRepository.save(roomMapperServ.mapToRepositoryRoom(room))
        );
    }

    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }
}
