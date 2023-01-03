package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.domain.Hotel;
import com.project.hotelBooking.domain.Localization;
import com.project.hotelBooking.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorTest {

    @InjectMocks
    private Validator validatorMock;
   @Mock
    private LocalizationService localizationService;
   @Mock
   private HotelService hotelService;

    @Test
    public void shouldNotReturnBadRequestExceptionValidateLocalization() {

        //given
        Localization localization = new Localization(1L, "Krakow", "Poland",null);
        when(localizationService.getLocalizationByCityAndCountry(any(Localization.class))).thenReturn(null);
        //when &then
        assertDoesNotThrow(()->validatorMock.validateLocalization(localization));
    }

    @Test
    public void shouldReturnBadRequestExceptionValidateLocalizationCity() {

        //given
        Localization localization = new Localization(1L, "K", "Poland",null);
        //when &then
        assertThrows(BadRequestException.class, ()->validatorMock.validateLocalization(localization));
    }

    @Test
    public void shouldReturnBadRequestExceptionValidateLocalizationCountry() {

        //given
        Localization localization = new Localization(1L, "Krakow", "P",null);
        //when & then
        assertThrows(BadRequestException.class, ()->validatorMock.validateLocalization(localization));
    }

    @Test
    public void shouldNotReturnBadRequestExceptionValidateHotel() {

        //given
        Hotel hotel = new Hotel(1L, "hotel1", 2, "Mariot", 2L,null);
        when(hotelService.getHotelByNameAndHotelChain(any(Hotel.class))).thenReturn(null);
        when(localizationService.getLocalizationById(any(Long.class))).thenReturn(Optional.of(new Localization()));
        //when &then
        assertDoesNotThrow(()->validatorMock.validateHotel(hotel));
    }

    @Test

    public void shouldReturnBadRequestExceptionValidateHotelNumberOfStars() {

        //given
        Hotel hotel = new Hotel(1L, "hotel1", 0, "Mariot", 2L,null);
        //when & then
        assertThrows(BadRequestException.class, ()->validatorMock.validateHotel(hotel));
    }


}