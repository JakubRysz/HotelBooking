package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.service.model.RoomServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapperServ {
    RoomServ mapToRoom(com.project.hotelBooking.repository.model.Room room);
}
