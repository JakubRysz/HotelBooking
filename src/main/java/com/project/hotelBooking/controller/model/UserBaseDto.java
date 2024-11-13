package com.project.hotelBooking.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@ToString
@SuperBuilder
public abstract class UserBaseDto {
    @JsonProperty
    String firstName;
    @JsonProperty
    String lastName;
    @JsonProperty
    LocalDate dateOfBirth;
    @JsonProperty
    String username;
    @JsonProperty
    String email;
}
