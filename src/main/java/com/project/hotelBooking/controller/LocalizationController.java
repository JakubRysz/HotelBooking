package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.mapper.LocalizationMapper;
import com.project.hotelBooking.controller.model.LocalizationDto;
import com.project.hotelBooking.controller.model.LocalizationWithHotelsDto;
import com.project.hotelBooking.service.LocalizationService;
import com.project.hotelBooking.service.model.LocalizationServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class LocalizationController {

    private final LocalizationService localizationService;
    private final LocalizationMapper localizationMapper;
    private final ValidatorCustom validatorCustom;

    @PostMapping("/localizations")
    @PreAuthorize("hasRole('ADMIN')")
    public LocalizationDto createLocalization(@RequestBody LocalizationDto localizationDto) {
        LocalizationServ localization = localizationMapper.mapToLocalization(localizationDto);
        validatorCustom.validateLocalization(localization);
        return localizationMapper.mapToLocalizationDto(localizationService.saveLocalization(
                localizationMapper.mapToLocalization(localizationDto))
        );
    }

    @GetMapping("/localizations/hotels/{id}")
    public LocalizationWithHotelsDto getSingleLocalizationWithHotels(@PathVariable Long id) {
        return localizationMapper.mapToLocalizationWithHotelsDto(localizationService.getLocalizationById(id));
    }

    @GetMapping("/localizations/hotels")
    public List<LocalizationWithHotelsDto> getLocalizationsWithHotels(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (localizationService.getLocalizationsWithHotels(page, sort).stream()
                .map(k -> localizationMapper.mapToLocalizationWithHotelsDto(k))
                .collect(Collectors.toList()));
    }

    @GetMapping("/localizations")
    public List<LocalizationDto> getLocalizations(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (localizationService.getLocalizations(page, sort).stream()
                .map(localizationMapper::mapToLocalizationDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/localizations")
    public LocalizationDto editLocalization(@RequestBody LocalizationDto localizationDto) {
        LocalizationServ localization = localizationMapper.mapToLocalization(localizationDto);
        validatorCustom.validateLocalizationEdit(localization);
        return localizationMapper.mapToLocalizationDto(localizationService.editLocalization(localization));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/localizations/{id}")
    public void deleteSingleLocalization(@PathVariable Long id) {
        validatorCustom.validateIfLocalizationExistById(id);
        localizationService.deleteLocalizationById(id);
    }

}
