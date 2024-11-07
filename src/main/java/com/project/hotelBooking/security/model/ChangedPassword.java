package com.project.hotelBooking.security.model;

import com.project.hotelBooking.controller.Annotations.ValidPassword;
import com.project.hotelBooking.controller.utils.PasswordUtils;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;

import static com.project.hotelBooking.controller.utils.PasswordUtils.PASSWORDS_DO_NOT_MATCH_MESSAGE;

@Getter
@Builder
public class ChangedPassword {
    @ValidPassword
    private String password;
    @ValidPassword
    private String confirmPassword;
    private String hash;

    @AssertTrue(message = PASSWORDS_DO_NOT_MATCH_MESSAGE)
    public boolean isPasswordAndConfirmPasswordMatching() {
        return PasswordUtils.isPasswordAndConfirmPasswordMatching(password, confirmPassword);
    }
}
