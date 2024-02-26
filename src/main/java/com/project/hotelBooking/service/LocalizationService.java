package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.domain.HotelDto;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.mapper.HotelMapper;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalizationService {

    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
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
        List<HotelDto> hotelDtos = hotelRepository.findAllByLocalizationIdIn(ids).stream().map(hotel -> hotelMapper.mapToHotelDto(hotel)).toList();
        List<Hotel> hotels =hotelDtos.stream().map(hotelDto -> hotelMapper.mapToHotel(hotelDto)).collect(Collectors.toList());
        localizations.forEach(localization -> localization.setHotels(extractHotels(hotels, localization.getId())));
        return localizations;
    }
    private List<Hotel> extractHotels(List<Hotel> hotels, Long id) {
        return hotels.stream()
                .filter(hotel -> hotel.getLocalizationId()==id).collect(Collectors.toList());
    }
    public Localization editLocalization(Localization localization) {
        return localizationRepository.save(localization);
    }
    public void deleteAllLocalizations() {
        localizationRepository.deleteAll();
    }

}
