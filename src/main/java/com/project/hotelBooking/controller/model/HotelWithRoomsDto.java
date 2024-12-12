package com.project.hotelBooking.controller.model;

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
