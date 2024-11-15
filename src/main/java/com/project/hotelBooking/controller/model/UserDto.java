package com.project.hotelBooking.controller.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class UserDto extends UserBaseDto {
    Long id;
    String role;
}