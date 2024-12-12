package com.project.hotelBooking.controller.model.hotel;

import com.project.hotelBooking.controller.model.room.RoomDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class HotelWithRoomsDto extends HotelDto {
    List<RoomDto> rooms;
}
