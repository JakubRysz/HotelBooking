package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.Hotel;
import com.project.hotelBooking.domain.HotelDto;
import com.project.hotelBooking.domain.HotelWithRoomsDto;
import com.project.hotelBooking.mapper.HotelMapper;
import com.project.hotelBooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    @Autowired
    private Validator validator;
    @Autowired
    private HotelMapper hotelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotels")
    public HotelDto createHotel(@RequestBody HotelWithRoomsDto hotelWithRoomsDto) {
        Hotel hotel = hotelMapper.mapToHotel(hotelWithRoomsDto);
        validator.validateHotel(hotel);
        return hotelMapper.mapToHotelDto(hotelService.saveHotel(hotel));
    }
    @GetMapping("/hotels/Rooms/{id}")
    public HotelWithRoomsDto getSingleHotelWithRooms(@PathVariable Long id) {
        return hotelMapper.mapToHotelWithRoomsDto(hotelService.getHotelById(id)
                .orElseThrow(()->new ElementNotFoundException("No such hotel")));
    }
    @GetMapping("/hotels/Rooms")
    public List<HotelWithRoomsDto> getHotelsWithRooms(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (hotelService.getHotelsWithRooms(page, sort)
                .stream().map(k -> hotelMapper.mapToHotelWithRoomsDto(k))
                .collect(Collectors.toList()));
    }
    @GetMapping("/hotels")
    public List<HotelDto> getHotels(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (hotelService.getHotels(page, sort).stream()
                .map(k -> hotelMapper.mapToHotelDto(k))
                .collect(Collectors.toList()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotels")
    public HotelDto editHotel(@RequestBody HotelDto hotelDto) {
        Hotel hotel = hotelMapper.mapToHotel(hotelDto);
        validator.validateHotelEdit(hotel);
        return hotelMapper.mapToHotelDto(hotelService.editHotel(hotel));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hotels/{id}")
    public void deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotelById(id);
    }
}
