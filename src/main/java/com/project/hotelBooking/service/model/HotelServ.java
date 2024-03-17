package com.project.hotelBooking.service.model;


import com.project.hotelBooking.repository.model.Room;
import lombok.*;

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
    List<Room> rooms;
}
