package com.project.hotelBooking.controller.model.localization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public class LocalizationBaseDto {
    String city;
    String country;
}
