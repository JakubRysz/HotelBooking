package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.mapper.HotelMapper;
import com.project.hotelBooking.controller.model.HotelDto;
import com.project.hotelBooking.controller.model.HotelWithRoomsDto;
import com.project.hotelBooking.service.HotelService;
import com.project.hotelBooking.service.model.HotelServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final Validator validator;
    private final HotelMapper hotelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotels")
    public HotelDto createHotel(@RequestBody HotelWithRoomsDto hotelWithRoomsDto) {
        HotelServ hotel = hotelMapper.mapToHotel(hotelWithRoomsDto);
        validator.validateHotel(hotel);
        return hotelMapper.mapToHotelDto(hotelService.saveHotel(hotel));
    }

    @GetMapping("/hotels/rooms/{id}")
    public HotelWithRoomsDto getSingleHotelWithRooms(@PathVariable Long id) {
        return hotelMapper.mapToHotelWithRoomsDto(hotelService.getHotelById(id));
    }

    @GetMapping("/hotels/rooms")
    public List<HotelWithRoomsDto> getHotelsWithRooms(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (hotelService.getHotelsWithRooms(page, sort)
                .stream().map(k -> hotelMapper.mapToHotelWithRoomsDto(k))
                .collect(Collectors.toList()));
    }

    @GetMapping("/hotels")
    public List<HotelDto> getHotels(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (hotelService.getHotels(page, sort).stream()
                .map(hotel -> hotelMapper.mapToHotelDto(hotel))
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotels")
    public HotelDto editHotel(@RequestBody HotelDto hotelDto) {
        HotelServ hotel = hotelMapper.mapToHotel(hotelDto);
        validator.validateHotelEdit(hotel);
        return hotelMapper.mapToHotelDto(hotelService.editHotel(hotel));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hotels/{id}")
    public void deleteHotel(@PathVariable Long id) {
        validator.validateIfHotelExistById(id);
        hotelService.deleteHotelById(id);
    }
}
