package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.controller.model.RoomDto;
import com.project.hotelBooking.controller.model.RoomWithBookingsDto;
import com.project.hotelBooking.controller.model.RoomWithBookingsWithoutUsersDto;
import com.project.hotelBooking.service.RoomService;
import com.project.hotelBooking.service.model.RoomServ;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final Validator validator;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms")
    public RoomDto createRoom(@RequestBody RoomWithBookingsDto roomWithBookingsDto) {
        RoomServ room = roomMapper.mapToRoom(roomWithBookingsDto);
        validator.validateRoom(room);
        return roomMapper.mapToRoomDto(roomService.saveRoom(room));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rooms/bookings/{id}")
    public RoomWithBookingsDto getSingleRoomwithBookings(@PathVariable Long id) {
        return roomMapper.mapToRoomWithBookingsDto(roomService.getRoomById(id));
    }

    @GetMapping("/rooms/bookings/withoutUsers/{id}")
    public RoomWithBookingsWithoutUsersDto getSingleRoomWithBookingsWithoutUsers(@PathVariable Long id) {
        return roomMapper.mapToRoomWithBookingsWithoutUsersDto(roomService.getRoomById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rooms/bookings")
    public List<RoomWithBookingsDto> getRoomsWithBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (roomService.getRoomsWithBookings(page, sort).stream()
                .map(room -> roomMapper.mapToRoomWithBookingsDto(room))
                .collect(Collectors.toList()));
    }

    @GetMapping("/rooms/bookings/withoutUsers")
    public List<RoomWithBookingsWithoutUsersDto> getRoomsWithBookingsWithoutUsers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (roomService.getRoomsWithBookings(page, sort).stream()
                .map(room -> roomMapper.mapToRoomWithBookingsWithoutUsersDto(room))
                .collect(Collectors.toList()));
    }

    @GetMapping("/rooms")
    public List<RoomDto> getRooms(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (roomService.getRooms(page, sort).stream().map(room -> roomMapper.mapToRoomDto(room)).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/rooms")
    public RoomDto editRoom(@RequestBody RoomDto roomDto) {
        RoomServ room = roomMapper.mapToRoom(roomDto);
        validator.validateRoomEdit(room);
        return roomMapper.mapToRoomDto(roomService.editRoom(room));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        validator.validateIfRoomExistById(id);
        roomService.deleteRoomById(id);
    }


}
