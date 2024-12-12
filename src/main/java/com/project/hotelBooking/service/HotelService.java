package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
import com.project.hotelBooking.service.mapper.RoomMapperServ;
import com.project.hotelBooking.service.model.HotelServ;
import com.project.hotelBooking.service.model.RoomServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomMapperServ roomMapperServ;
    private static final int PAGE_SIZE=5;
    private final HotelMapperServ hotelMapperServ;

    public HotelServ getHotelByNameAndHotelChain(HotelServ hotel) {
        return hotelMapperServ.mapToHotel(hotelRepository.findHotelByNameAndHotelChain(
            hotel.getName(), hotel.getHotelChain()).orElseThrow(
                        ()->new ElementNotFoundException("No such hotel"))
        );
    }
    public HotelServ saveHotel(HotelServ hotel) {
        return hotelMapperServ.mapToHotel(
                hotelRepository.save(hotelMapperServ.mapToRepositoryHotel(hotel))
        );
    }
    public HotelServ getHotelByIdWithRooms(Long id) {

        return hotelMapperServ.mapToHotel(
                hotelRepository.findWithRoomsById(id)
                        .orElseThrow(() -> new ElementNotFoundException("No such hotel"))
        );
    }
    public HotelServ getHotelById(Long id) {

        return hotelMapperServ.mapToHotel(
                hotelRepository.findWithoutRoomsById(id)
                        .orElseThrow(() -> new ElementNotFoundException("No such hotel"))
        );
    }
    public void deleteHotelById(Long id) {
        hotelRepository.deleteById(id);
    }
    public List<HotelServ> getHotels(Integer page, Sort.Direction sort) {
        return hotelMapperServ.mapToHotels(
                hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }
    public List<HotelServ> getHotelsWithRooms(Integer page, Sort.Direction sort) {
        List<HotelServ> hotels =  hotelMapperServ.mapToHotels(
                hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
        List<Long> ids = hotels.stream().map(HotelServ::getId).collect(Collectors.toList());
        List<RoomServ> rooms = roomMapperServ.mapToRoms(
                roomRepository.findAllByHotelIdIn(ids).stream().toList());
        hotels.forEach(hotel -> hotel.withRooms(extractRooms(rooms, hotel.getId())));
        return hotels;
    }
    private List<RoomServ> extractRooms(List<RoomServ> rooms, Long id) {
        return rooms.stream()
                .filter(room -> Objects.equals(room.getHotelId(), id)).collect(Collectors.toList());
    }
    public HotelServ editHotel(HotelServ hotel) {
        return hotelMapperServ.mapToHotel(
                hotelRepository.save(hotelMapperServ.mapToRepositoryHotel(hotel))
        );
    }
    public void deleteAllHotels() {
        hotelRepository.deleteAll();
    }
}
