package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HotelWithRoomsDto {
    Long id;
    String name;
    int numberOfStars;
    String hotelChain;
    Long localizationId;
    List<RoomDto> rooms;
}
