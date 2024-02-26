package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Room;
import com.project.hotelBooking.domain.RoomDto;
import com.project.hotelBooking.domain.RoomWithBookingsDto;
import com.project.hotelBooking.domain.RoomWithBookingsWithoutUsersDto;
import org.mapstruct.Mapper;

@Mapper(uses = BookingMapper.class, componentModel = "spring")
public interface RoomMapper {
    Room mapToRoom(RoomDto roomDto);
    RoomDto mapToRoomDto(Room room);
    RoomWithBookingsDto mapToRoomWithBookingsDto(Room room);
    Room mapToRoom(RoomWithBookingsDto roomWithBookingsDto);
    RoomWithBookingsWithoutUsersDto mapToRoomWithBookingsWithoutUsersDto(Room room);
}
