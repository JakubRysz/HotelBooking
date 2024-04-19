package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
import com.project.hotelBooking.service.mapper.LocalizationMapperServ;
import com.project.hotelBooking.service.model.HotelServ;
import com.project.hotelBooking.service.model.LocalizationServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalizationService {

    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final LocalizationMapperServ localizationMapperServ;
    private final HotelMapperServ hotelMapperServ;
    private static final int PAGE_SIZE=5;

    public LocalizationServ getLocalizationByCityAndCountry(LocalizationServ localization) {
        return localizationMapperServ.mapToLocalization(localizationRepository.findLocalizationByCityAndCountry(
            localization.getCity(), localization.getCountry()).orElseThrow(
                ()->new ElementNotFoundException("No such localization"))
        );
    }
    public LocalizationServ saveLocalization(LocalizationServ localization) {
        return localizationMapperServ.mapToLocalization(
                localizationRepository.save(localizationMapperServ.mapToRepositoryLocalization(localization))
        );
    }
    public LocalizationServ getLocalizationById(Long id) {
        return localizationMapperServ.mapToLocalization(
                localizationRepository.findById(id).orElseThrow(
                        () -> new ElementNotFoundException("No such localization"))
        );
    }
    public void deleteLocalizationById(Long id) {
        localizationRepository.deleteById(id);
    }
    public List<LocalizationServ> getLocalizations(Integer page, Sort.Direction sort) {
        return localizationMapperServ.mapToLocalizations(
                localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }
    public List<LocalizationServ> getLocalizationsWithHotels(Integer page, Sort.Direction sort) {
        List<LocalizationServ> localizations =  localizationMapperServ.mapToLocalizations(
                localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
        List<Long> localizationIds = localizations.stream()
                .map(LocalizationServ::getId)
                .collect(Collectors.toList());
        List<HotelServ> hotels = hotelMapperServ.mapToHotels(
                hotelRepository.findAllByLocalizationIdIn(localizationIds).stream().toList()
        );
        localizations.forEach(localization -> localization.withHotels(extractHotels(hotels, localization.getId())));
        return localizations;
    }

    private List<HotelServ> extractHotels(List<HotelServ> hotels, Long id) {
        return hotels.stream()
                .filter(hotel -> Objects.equals(hotel.getLocalizationId(), id))
                .collect(Collectors.toList());
    }
    public LocalizationServ editLocalization(LocalizationServ localization) {
        return localizationMapperServ.mapToLocalization(
                localizationRepository.save(localizationMapperServ.mapToRepositoryLocalization(localization))
        );
    }
    public void deleteAllLocalizations() {
        localizationRepository.deleteAll();
    }

}
