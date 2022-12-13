package com.project.hotelBooking.repository;

import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.Room;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b")
    List<Booking> findAllBookings(Pageable page);

    @Query("select b from Booking b where b.roomId in :ids")
    List<Booking> findAllByRoomIdIn(@Param("ids") List<Long> list);
    @Query("select b from Booking b where b.userId in :ids")
    List<Booking> findAllByUserIdIn(@Param("ids") List<Long> ids);
}
