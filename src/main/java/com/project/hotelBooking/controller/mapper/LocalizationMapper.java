package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.localization.LocalizationDto;
import com.project.hotelBooking.controller.model.localization.LocalizationWithHotelsDto;
import com.project.hotelBooking.service.model.LocalizationServ;
import org.mapstruct.Mapper;

@Mapper(uses = HotelMapper.class, componentModel = "spring")
public interface LocalizationMapper {
    LocalizationServ mapToLocalization(LocalizationDto localizationDto);
    LocalizationDto mapToLocalizationDto(LocalizationServ localization);
    LocalizationWithHotelsDto mapToLocalizationWithHotelsDto(LocalizationServ localization);

}
