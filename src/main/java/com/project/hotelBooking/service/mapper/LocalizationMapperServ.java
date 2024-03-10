package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.service.model.LocalizationServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocalizationMapperServ {
    LocalizationServ mapToLocalization(com.project.hotelBooking.repository.model.Localization localization);
}
