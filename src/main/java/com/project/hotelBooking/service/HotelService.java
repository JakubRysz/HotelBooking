package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.repository.model.Room;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
import com.project.hotelBooking.service.model.HotelServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
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
    public HotelServ getHotelById(Long id) {

        return hotelMapperServ.mapToHotel(
                hotelRepository.findById(id)
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
        List<Room> rooms = roomRepository.findAllByHotelIdIn(ids).stream().toList();
        hotels.forEach(hotel -> hotel.withRooms(extractRooms(rooms, hotel.getId())));
        return hotels;
    }
    private List<Room> extractRooms(List<Room> rooms, Long id) {
        return rooms.stream()
                .filter(room -> room.getHotelId()==id).collect(Collectors.toList());
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
