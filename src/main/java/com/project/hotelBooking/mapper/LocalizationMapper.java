package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LocalizationMapper {

    @Autowired
    HotelMapper hotelMapper;

    public Localization mapToLocalization(LocalizationDto localizationDto){
        return new Localization(
                localizationDto.getId(),
                localizationDto.getCity(),
                localizationDto.getCountry(),
                null
        );
    }


    public LocalizationDto mapToLocalizationDto(Localization localization){
        return new LocalizationDto(
                localization.getId(),
                localization.getCity(),
                localization.getCountry()
        );
    }

    public LocalizationWithHotelsDto mapToLocalizationWithHotelsDto(Localization localization) {
        return new LocalizationWithHotelsDto(
                localization.getId(),
                localization.getCity(),
                localization.getCountry(),
                localization.getHotels().stream().
                        map(hotel -> hotelMapper.mapToHotelDto(hotel))
                                .collect(Collectors.toList()));
    }
}
