package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.controller.model.LocalizationDto;
import com.project.hotelBooking.controller.model.LocalizationWithHotelsDto;
import org.mapstruct.Mapper;

@Mapper(uses = HotelMapper.class, componentModel = "spring")
public interface LocalizationMapper {
    Localization mapToLocalization(LocalizationDto localizationDto);
    LocalizationDto mapToLocalizationDto(Localization localization);
    LocalizationWithHotelsDto mapToLocalizationWithHotelsDto(Localization localization);

}
