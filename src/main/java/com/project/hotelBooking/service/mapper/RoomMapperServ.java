package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.repository.model.Room;
import com.project.hotelBooking.service.model.RoomServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapperServ {
    RoomServ mapToRoom(Room room);
    List<RoomServ> mapToRoms(List<Room> rooms);
    Room mapToRepositoryRoom(RoomServ room);
}
