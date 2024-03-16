package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.service.model.HotelServ;
import com.project.hotelBooking.service.model.LocalizationServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalizationMapperServ {
    LocalizationServ mapToLocalization(Localization localization);
    List<LocalizationServ> mapToLocalizations(List<Localization> localizations);
    Localization mapToRepositoryLocalization(LocalizationServ localization);
}
