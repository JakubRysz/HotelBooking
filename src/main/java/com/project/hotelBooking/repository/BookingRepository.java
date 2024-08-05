package com.project.hotelBooking.repository;

import com.project.hotelBooking.repository.model.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b")
    List<Booking> findAllBookings(Pageable page);

    List<Booking> findAllByUserId(Long id, Pageable page);

    @Query("select b from Booking b where b.roomId in :ids")
    List<Booking> findAllByRoomIdIn(@Param("ids") List<Long> list);

    @Query("select b from Booking b where b.userId in :ids")
    List<Booking> findAllByUserIdIn(@Param("ids") List<Long> ids);
}
