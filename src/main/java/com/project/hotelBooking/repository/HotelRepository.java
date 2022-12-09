package com.project.hotelBooking.repository;

import com.project.hotelBooking.domain.Hotel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    //List<Hotel> findAll();
    @Query("SELECT h from Hotel h")
    List<Hotel> findAllHotels(Pageable page);

    @Query("select h from Hotel h where h.localizationId in :ids")
    List<Hotel> findAllByLocalizationIdIn(@Param("ids") List<Long> list);

}
