package com.project.hotelBooking.service.mapper;


import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.service.model.HotelServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HotelMapperServ {
    HotelServ mapToHotel(Hotel hotel);
    List<HotelServ> mapToHotels(List<Hotel> hotels);
    Hotel mapToRepositoryHotel(HotelServ hotel);

}
