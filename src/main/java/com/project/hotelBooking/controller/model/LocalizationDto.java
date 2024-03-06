package com.project.hotelBooking.controller.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LocalizationDto {
    private Long id;
    private String city;
    private String country;
}
