package com.project.hotelBooking.controller;

import com.project.hotelBooking.domain.Booking;

import java.util.List;

public class Validator {

    public static boolean validateIfRoomFree(List<Booking> bookingsDatabase, Booking bookingNew) {

        boolean roomIsFree = false;

        if (bookingsDatabase.size() > 0) {

            for (Booking bookingDatabase : bookingsDatabase) {
                if (bookingDatabase.getEnd_date().isAfter(bookingNew.getStart_date())) {
                    if (bookingDatabase.getStart_date().isBefore(bookingNew.getEnd_date())) return false;
                }
            }
            return true;
        }
        return true;
    }

    public static boolean validateDate(Booking bookingNew) {
        if (bookingNew.getStart_date().isBefore(bookingNew.getEnd_date())) return true;
        return false;
    }

}