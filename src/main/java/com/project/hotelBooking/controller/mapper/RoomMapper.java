package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.room.RoomDto;
import com.project.hotelBooking.controller.model.room.RoomWithBookingsDto;
import com.project.hotelBooking.controller.model.room.RoomWithBookingsWithoutUsersDto;
import com.project.hotelBooking.service.model.RoomServ;
import org.mapstruct.Mapper;

@Mapper(uses = BookingMapper.class, componentModel = "spring")
public interface RoomMapper {
    RoomServ mapToRoom(RoomDto roomDto);
    RoomDto mapToRoomDto(RoomServ room);
    RoomWithBookingsDto mapToRoomWithBookingsDto(RoomServ room);
    RoomServ mapToRoom(RoomWithBookingsDto roomWithBookingsDto);
    RoomWithBookingsWithoutUsersDto mapToRoomWithBookingsWithoutUsersDto(RoomServ room);
}
