package com.project.hotelBooking.controller.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class UserCreateAdminDto extends UserCreateDto {
    String role;
}