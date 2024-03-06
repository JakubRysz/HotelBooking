package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.controller.model.HotelDto;
import com.project.hotelBooking.controller.model.HotelWithRoomsDto;
import org.mapstruct.Mapper;

@Mapper(uses = RoomMapper.class, componentModel = "spring")
public interface HotelMapper {
    Hotel mapToHotel(HotelDto hotelDto);
    HotelDto mapToHotelDto(Hotel hotel);
    HotelWithRoomsDto mapToHotelWithRoomsDto(Hotel hotel);
    Hotel mapToHotel(HotelWithRoomsDto hotelWithRoomsDto);
}
