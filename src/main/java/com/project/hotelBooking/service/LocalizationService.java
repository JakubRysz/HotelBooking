package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.repository.*;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
import com.project.hotelBooking.service.model.HotelServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalizationService {

    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final HotelMapperServ hotelMapperServ;
    private static final int PAGE_SIZE=5;

    public Optional<Localization> getLocalizationByCityAndCountry(Localization localization) { return localizationRepository.findLocalizationByCityAndCountry(
            localization.getCity(), localization.getCountry());}
    public Localization saveLocalization(Localization localization) {
        return localizationRepository.save(localization);
    }
    public Optional<Localization> getLocalizationById(Long id) {
        return localizationRepository.findById(id);
    }
    public void deleteLocalizationById(Long id) {
        localizationRepository.deleteById(id);
    }
    public List<Localization> getLocalizations(Integer page, Sort.Direction sort) {
        return localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<Localization> getLocalizationsWithHotels(Integer page, Sort.Direction sort) {
        List<Localization> localizations =  localizationRepository.findAllLocalizations(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = localizations.stream().map(Localization::getId).collect(Collectors.toList());
        List<HotelServ> hotels = hotelMapperServ.mapToHotels(hotelRepository.findAllByLocalizationIdIn(ids).stream().toList());
        localizations.forEach(localization -> localization.setHotels(extractHotels(hotels, localization.getId())));
        return localizations;
    }

    //TODO - remove mapper after adding LocalizationServ
    private List<Hotel> extractHotels(List<HotelServ> hotels, Long id) {
        return hotels.stream()
                .filter(hotel -> Objects.equals(hotel.getLocalizationId(), id))
                .map(hotelMapperServ::mapToRepositoryHotel)
                .collect(Collectors.toList());
    }
    public Localization editLocalization(Localization localization) {
        return localizationRepository.save(localization);
    }
    public void deleteAllLocalizations() {
        localizationRepository.deleteAll();
    }

}
