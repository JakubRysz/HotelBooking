package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.controller.model.LocalizationDto;
import com.project.hotelBooking.controller.model.LocalizationWithHotelsDto;
import com.project.hotelBooking.service.model.LocalizationServ;
import org.mapstruct.Mapper;

@Mapper(uses = HotelMapper.class, componentModel = "spring")
public interface LocalizationMapper {
    LocalizationServ mapToLocalization(LocalizationDto localizationDto);
    LocalizationDto mapToLocalizationDto(LocalizationServ localization);
    LocalizationWithHotelsDto mapToLocalizationWithHotelsDto(LocalizationServ localization);

}
