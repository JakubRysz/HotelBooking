package com.project.hotelBooking.controller.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public abstract class BookingBaseDto {
    Long roomId;
    LocalDate startDate;
    LocalDate endDate;
}
