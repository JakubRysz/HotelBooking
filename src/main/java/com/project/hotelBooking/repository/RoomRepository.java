package com.project.hotelBooking.repository;

import com.project.hotelBooking.repository.model.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface  RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT r from Room r")
    List<Room> findAllRooms(Pageable page);

    @Query("select r from Room r where r.hotelId in :ids")
    List<Room> findAllByHotelIdIn(@Param("ids") List<Long> list);

    @Query("SELECT r.id FROM Room r WHERE r.hotelId = :id")
    List<Long> findAllIdsByHotelId(@Param("id") Long id);

    Optional<Room> findRoomByRoomNumberAndHotelId(int roomNumber, Long hotelId);
}
