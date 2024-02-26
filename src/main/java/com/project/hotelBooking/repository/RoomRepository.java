package com.project.hotelBooking.repository;

import com.project.hotelBooking.repository.model.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface  RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT r from Room r")
    List<Room> findAllRooms(Pageable page);

    @Query("select h from Room h where h.hotelId in :ids")
    List<Room> findAllByHotelIdIn(@Param("ids") List<Long> list);

    Optional<Room> findRoomByRoomNumberAndHotelId(int roomNumber, Long hotelId);
}
