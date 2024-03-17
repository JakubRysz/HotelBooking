package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDto {
        private Long id;
        private String name;
        private int numberOfStars;
        private String hotelChain;
        private Long localizationId;
}

