package com.project.hotelBooking.repository;

import com.project.hotelBooking.repository.model.Localization;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LocalizationRepository extends JpaRepository<Localization, Long> {
    @Query("SELECT l from Localization l")
    List<Localization> findAllLocalizations(Pageable page);
    Optional<Localization> findLocalizationByCityAndCountry(String cityName, String CountryName);
}

