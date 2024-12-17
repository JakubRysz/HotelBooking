package com.project.hotelBooking.controller.model.localization;

import com.project.hotelBooking.controller.model.hotel.HotelDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class LocalizationWithHotelsDto extends LocalizationDto{
    List<HotelDto> hotels;
}
