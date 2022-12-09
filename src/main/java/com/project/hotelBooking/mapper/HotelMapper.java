package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.Hotel;
import com.project.hotelBooking.domain.HotelDto;
import com.project.hotelBooking.domain.HotelWithRoomsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HotelMapper {

    @Autowired
    private RoomMapper roomMapper;

    public Hotel mapToHotel(HotelDto hotelDto) {
        return new Hotel(
                hotelDto.getId(),
                hotelDto.getName(),
                hotelDto.getNumberOfStars(),
                hotelDto.getHotelChain(),
                hotelDto.getLocalizationId(),
                null
        );
    }
    public HotelDto mapToHotelDto(Hotel hotel) {
        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getNumberOfStars(),
                hotel.getHotelChain(),
                hotel.getLocalizationId()
        );
    }
    public HotelWithRoomsDto mapToHotelWithRoomsDto(Hotel hotel) {
        return new HotelWithRoomsDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getNumberOfStars(),
                hotel.getHotelChain(),
                hotel.getLocalizationId(),
                hotel.getRooms().stream().map(room -> roomMapper.mapToRoomDto(room)).collect(Collectors.toList())
        );
    }
    public Hotel mapToHotel(HotelWithRoomsDto hotelWithRoomsDto) {
        return new Hotel(
                hotelWithRoomsDto.getId(),
                hotelWithRoomsDto.getName(),
                hotelWithRoomsDto.getNumberOfStars(),
                hotelWithRoomsDto.getHotelChain(),
                hotelWithRoomsDto.getLocalizationId(),
                null
        );
    }
}