package com.project.hotelBooking.service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class HotelServ {
    Long id;
    String name;
    int numberOfStars;
    String hotelChain;
    Long localizationId;
    @With
    List<RoomServ> rooms;
}
