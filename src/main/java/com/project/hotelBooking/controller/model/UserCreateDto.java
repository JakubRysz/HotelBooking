package com.project.hotelBooking.controller.model;

import com.project.hotelBooking.controller.Annotations.ValidPassword;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Objects;

@Value
@Builder
public class UserCreateDto {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String username;
    @ValidPassword
    String password;
    @ValidPassword
    String confirmPassword;
    String role;
    String email;

    @AssertTrue(message = "passwords do not match")
    public boolean isPasswordAndConfirmPasswordMatching() {
        // if password or confirmPassword is null they should not be compared
        if (Objects.isNull(password) || Objects.isNull(confirmPassword))
            return true;
        return password.equals(confirmPassword);
    }
}