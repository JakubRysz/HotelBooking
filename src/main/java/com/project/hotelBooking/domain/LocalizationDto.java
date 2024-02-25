package com.project.hotelBooking.domain;

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
