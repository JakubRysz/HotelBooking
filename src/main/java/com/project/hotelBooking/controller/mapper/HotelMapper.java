package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.HotelDto;
import com.project.hotelBooking.controller.model.HotelWithRoomsDto;
import com.project.hotelBooking.service.model.HotelServ;
import org.mapstruct.Mapper;

@Mapper(uses = RoomMapper.class, componentModel = "spring")
public interface HotelMapper {
    HotelServ mapToHotel(HotelDto hotelDto);
    HotelDto mapToHotelDto(HotelServ hotel);
    HotelWithRoomsDto mapToHotelWithRoomsDto(HotelServ hotel);
    HotelServ mapToHotel(HotelWithRoomsDto hotelWithRoomsDto);
}
