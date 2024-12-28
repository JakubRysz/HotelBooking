package com.project.hotelBooking.controller.model.localization;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class LocalizationDto  extends LocalizationBaseDto{
    Long id;
}
