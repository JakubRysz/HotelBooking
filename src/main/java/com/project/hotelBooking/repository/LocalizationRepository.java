package com.project.hotelBooking.repository;

import com.project.hotelBooking.domain.Localization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface LocalizationRepository extends JpaRepository<Localization, Long> {
    @Query("SELECT l from Localization l")
    List<Localization> findAllLocalizations(Pageable page);
}

