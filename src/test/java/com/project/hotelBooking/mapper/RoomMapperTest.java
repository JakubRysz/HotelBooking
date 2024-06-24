package com.project.hotelBooking.mapper;

import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.controller.model.RoomDto;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.RoomServ;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RoomMapperTest {
    private static final Long ROOM_ID = 1L;
    private static final int ROOM_NUMBER = 2;
    private static final List<BookingServ> BOOKINGS = List.of();
    private static final Long HOTEL_ID = 3L;
    private static final int NUMBER_OF_PERSONS = 2;
    private static final int STANDARD = 2;

    private final RoomMapper roomMapper;

    @Test
    public void shouldMapToRoomDto() {
        //given
        RoomServ room = RoomServ.builder()
                .id(ROOM_ID)
                .roomNumber(ROOM_NUMBER)
                .bookings(BOOKINGS)
                .hotelId(HOTEL_ID)
                .numberOfPersons(NUMBER_OF_PERSONS)
                .standard(STANDARD)
                .build();

        RoomDto expectedRoomDto = RoomDto.builder()
                .id(ROOM_ID)
                .roomNumber(ROOM_NUMBER)
                .hotelId(HOTEL_ID)
                .numberOfPersons(NUMBER_OF_PERSONS)
                .standard(STANDARD)
                .build();


        //when
        RoomDto roomDto = roomMapper.mapToRoomDto(room);
        //then
        assertThat(roomDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedRoomDto);
    }
}
