package com.project.hotelBooking.service;

import com.project.hotelBooking.domain.Hotel;
import com.project.hotelBooking.domain.Room;
import com.project.hotelBooking.domain.RoomDto;
import com.project.hotelBooking.mapper.RoomMapper;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private static final int PAGE_SIZE=5;

    public Hotel getHotelByNameAndHotelChain(Hotel hotel) { return hotelRepository.findHotelByNameAndHotelChain(
            hotel.getName(), hotel.getHotelChain());}
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }
    public void deleteHotelById(Long id) {
        hotelRepository.deleteById(id);
    }
    public List<Hotel> getHotels(Integer page, Sort.Direction sort) {
        return hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Hotel> getHotelsWithRooms(Integer page, Sort.Direction sort) {
        List<Hotel> hotels =  hotelRepository.findAllHotels(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = hotels.stream().map(Hotel::getId).collect(Collectors.toList());
        List<RoomDto> roomDtos = roomRepository.findAllByHotelIdIn(ids).stream().map(room -> roomMapper.mapToRoomDto(room)).toList();
        List<Room> rooms =roomDtos.stream().map(roomDto ->roomMapper.mapToRoom(roomDto)).collect(Collectors.toList());
        hotels.forEach(hotel -> hotel.setRooms(extractRooms(rooms, hotel.getId())));
        return hotels;
    }
    private List<Room> extractRooms(List<Room> rooms, Long id) {
        return rooms.stream()
                .filter(room -> room.getHotelId()==id).collect(Collectors.toList());
    }
    public Hotel editHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
    public void deleteAllHotels() {
        hotelRepository.deleteAll();
    }
}
