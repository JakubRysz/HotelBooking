package com.project.hotelBooking.controller.model.hotel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class HotelCreateDto extends HotelBaseDto {
}
