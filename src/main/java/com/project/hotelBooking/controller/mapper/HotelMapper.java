package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.hotel.HotelCreateDto;
import com.project.hotelBooking.controller.model.hotel.HotelDto;
import com.project.hotelBooking.controller.model.hotel.HotelWithRoomsDto;
import com.project.hotelBooking.service.model.HotelServ;
import org.mapstruct.Mapper;

@Mapper(uses = RoomMapper.class, componentModel = "spring")
public interface HotelMapper {
    HotelServ mapToHotel(HotelDto hotelDto);
    HotelDto mapToHotelDto(HotelServ hotel);
    HotelCreateDto mapToHotelCreateDto(HotelServ hotel);
    HotelWithRoomsDto mapToHotelWithRoomsDto(HotelServ hotel);
    HotelServ mapToHotel(HotelWithRoomsDto hotelWithRoomsDto);
}
