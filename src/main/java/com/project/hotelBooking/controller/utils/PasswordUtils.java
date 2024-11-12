package com.project.hotelBooking.controller.utils;

import java.util.Objects;

public class PasswordUtils {

    public static final String PASSWORDS_DO_NOT_MATCH_MESSAGE = "passwords do not match";

    public static boolean isPasswordAndConfirmPasswordMatching(String password, String confirmPassword) {
        // if password or confirmPassword is null they should not be compared
        if (Objects.isNull(password) || Objects.isNull(confirmPassword))
            return true;
        return password.equals(confirmPassword);
    }
}
