package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.Room;
import com.project.hotelBooking.domain.RoomDto;
import com.project.hotelBooking.domain.RoomWithBookingsDto;
import com.project.hotelBooking.domain.RoomWithBookingsWithoutUsersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class RoomMapper {

    @Autowired
    BookingMapper bookingMapper;

    public Room mapToRoom(RoomDto roomDto) {
        return new Room(
                roomDto.getId(),
                roomDto.getRoomNumber(),
                roomDto.getNumberOfPersons(),
                roomDto.getStandard(),
                roomDto.getHotelId(),
                null
        );
    }
    public RoomDto mapToRoomDto (Room room){
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getNumberOfPersons(),
                room.getStandard(),
                room.getHotelId()
        );
    }
    public RoomWithBookingsDto mapToRoomWithBookingsDto(Room room){
        return new RoomWithBookingsDto(
                room.getId(),
                room.getRoomNumber(),
                room.getNumberOfPersons(),
                room.getStandard(),
                room.getHotelId(),
                room.getBookings().stream().map(booking -> bookingMapper.mapToBookingDto(booking)).collect(Collectors.toList())
        );
    }
    public Room mapToRoom(RoomWithBookingsDto roomWithBookingsDto) {
        return new Room(
                roomWithBookingsDto.getId(),
                roomWithBookingsDto.getRoomNumber(),
                roomWithBookingsDto.getNumberOfPersons(),
                roomWithBookingsDto.getStandard(),
                roomWithBookingsDto.getHotelId(),
                null
        );
    }

    public RoomWithBookingsWithoutUsersDto mapToRoomWithBookingsWithoutUsersDto(Room room) {
        return new RoomWithBookingsWithoutUsersDto(
                room.getId(),
                room.getRoomNumber(),
                room.getNumberOfPersons(),
                room.getStandard(),
                room.getHotelId(),
                room.getBookings().stream().map(booking -> bookingMapper.mapToBookingWithoutUserDto(booking)).collect(Collectors.toList())
        );
    }
}

