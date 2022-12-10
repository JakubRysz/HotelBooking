package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class HotelBookingDbServiceTestSuite {

    @Autowired
    private DbService dbService;

    @Test
    public void testSaveGetDeleteLocalization() {

        Localization localization1 = new Localization(null, "Krakow", "Poland",null);

        //test save
        Localization localizationSaved1 = dbService.saveLocalization(localization1);
        assertNotEquals(null,localizationSaved1.getId());
        assertEquals(localizationSaved1.getCity(), localization1.getCity());
        assertEquals(localizationSaved1.getCountry(), localization1.getCountry());


        //test get
        assertEquals(localizationSaved1.getId(),localizationSaved1.getId());
        Localization localizationGet1 = new Localization();
        localizationGet1 = dbService.getLocalizationById(localizationSaved1.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));

        assertNotEquals(null,localizationGet1.getId());
        assertEquals(localization1.getCity(), localizationGet1.getCity());
        assertEquals(localization1.getCountry(), localizationGet1.getCountry());

        // test delete
        int sizeBeforeDelete = dbService.getLocalizations(0, Sort.Direction.ASC).size();
        dbService.deleteLocalizationById(localizationGet1.getId());
        int sizeAfterDelete = dbService.getLocalizations(0, Sort.Direction.ASC).size();
        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);
    }

    @Test
    public void testSaveGetDeleteHotel() {

        Localization localization1 = new Localization(null, "Krakow", "Poland",null);
        Localization localizationSaved1 = dbService.saveLocalization(localization1);
        Hotel hotel1 = new Hotel(null, "hotel1", 2, "Mariot", localizationSaved1.getId(),null);

        //test save
        Hotel hotelSaved1 = dbService.saveHotel(hotel1);
        assertNotEquals(null,hotelSaved1.getId());
        assertEquals(hotel1.getName(), hotelSaved1.getName());
        assertEquals(hotel1.getNumberOfStars(), hotelSaved1.getNumberOfStars());
        assertEquals(hotel1.getHotelChain(), hotelSaved1.getHotelChain());
        assertEquals(hotel1.getLocalizationId(), hotelSaved1.getLocalizationId());

        //test get
        Hotel hotelGet1 = new Hotel();
        hotelGet1 = dbService.getHotelById(hotelSaved1.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));

        assertNotEquals(null, hotelGet1.getId());
        assertEquals(hotel1.getName(), hotel1.getName());
        assertEquals(hotel1.getNumberOfStars(), hotelGet1.getNumberOfStars());
        assertEquals(hotel1.getHotelChain(), hotelGet1.getHotelChain());
        assertEquals(hotel1.getLocalizationId(), hotelGet1.getLocalizationId());

        // test delete
        int sizeBeforeDelete = dbService.getHotels(0,Sort.Direction.ASC).size();
        dbService.deleteHotelById(hotelGet1.getId());
        int sizeAfterDelete = dbService.getHotels(0,Sort.Direction.ASC).size();
        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);

        dbService.deleteLocalizationById(localizationSaved1.getId());
    }

    @Test
    public void testSaveGetDeleteRoom () {

        Localization localization1 = new Localization(null, "Krakow", "Poland",null);
        Localization localizationSaved1 = dbService.saveLocalization(localization1);
        Hotel hotel1 = new Hotel(null, "hotel1", 2, "Mariot", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = dbService.saveHotel(hotel1);
        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);

        //test save
        Room roomSaved1 = dbService.saveRoom(room1);

        assertNotEquals(null,roomSaved1.getId());
        assertEquals(room1.getRoomNumber(), roomSaved1.getRoomNumber());
        assertEquals(room1.getNumberOfPersons(), roomSaved1.getNumberOfPersons());
        assertEquals(room1.getStandard(), roomSaved1.getStandard());
        assertEquals(room1.getHotelId(), roomSaved1.getHotelId());

        //test get
        Room roomGet1 = new Room();
        roomGet1 = dbService.getRoomById(roomSaved1.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));

        assertNotEquals(null, roomGet1.getId());
        assertEquals(room1.getRoomNumber(), roomGet1.getRoomNumber());
        assertEquals(room1.getNumberOfPersons(), roomGet1.getNumberOfPersons());
        assertEquals(room1.getStandard(), roomGet1.getStandard());
        assertEquals(room1.getHotelId(), roomGet1.getHotelId());

        // test delete
        int sizeBeforeDelete = dbService.getRooms(0,Sort.Direction.ASC).size();
        dbService.deleteRoomById(roomGet1.getId());
        int sizeAfterDelete = dbService.getRooms(0,Sort.Direction.ASC).size();
        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);

        dbService.deleteHotelById(hotelSaved1.getId());
        dbService.deleteLocalizationById(localizationSaved1.getId());
    }

    @Test
    public void testSaveGetDeleteUser () {

        User user1 = new User(null, "Jan", "Kowalski", LocalDate.of(1979, 1, 10),null);
        //test save
        User userSaved1 = dbService.saveUser(user1);

        assertNotEquals(null,userSaved1.getId());
        assertEquals(user1.getFirstName(), userSaved1.getFirstName());
        assertEquals(user1.getLastName(), userSaved1.getLastName());
        assertEquals(user1.getDateOfBirth(), userSaved1.getDateOfBirth());

        //test get
        User userGet1 = new User();
        userGet1 = dbService.getUserById(userSaved1.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));

        assertNotEquals(null, userGet1.getId());
        assertEquals(user1.getFirstName(), userGet1.getFirstName());
        assertEquals(user1.getLastName(), userGet1.getLastName());
        assertEquals(user1.getDateOfBirth(), userGet1.getDateOfBirth());

        // test delete
        int sizeBeforeDelete = dbService.getUsers(0,Sort.Direction.ASC).size();
        dbService.deleteUserById(userGet1.getId());
        int sizeAfterDelete = dbService.getUsers(0,Sort.Direction.ASC).size();
        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);
    }

    @Test
    public void testSaveGetDeleteBooking () {

        Localization localization1 = new Localization(null, "Krakow", "Poland",null);
        Localization localizationSaved1 = dbService.saveLocalization(localization1);
        Hotel hotel1 = new Hotel(null, "hotel1", 2, "Mariot", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = dbService.saveHotel(hotel1);
        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);
        Room roomSaved1 = dbService.saveRoom(room1);
        User user1 = new User(null, "Jan", "Kowalski", LocalDate.of(1979, 1, 10),null);
        User userSaved1 = dbService.saveUser(user1);
        Booking booking1 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2022, 11, 17), LocalDate.of(2022, 11, 21));

        //test save
        Booking bookingSaved1 = dbService.saveBooking(booking1);

        assertNotEquals(null,bookingSaved1.getId());
        assertEquals(booking1.getUserId(), bookingSaved1.getUserId());
        assertEquals(booking1.getRoomId() , bookingSaved1.getRoomId());
        assertEquals(booking1.getStart_date(), bookingSaved1.getStart_date());
        assertEquals(booking1.getEnd_date(), bookingSaved1.getEnd_date());

        //test get
        Booking bookingGet1 = new Booking();
        bookingGet1 = dbService.getBookingById(bookingSaved1.getId()).orElseThrow(()->new ElementNotFoundException("No such element"));

        assertNotEquals(null, bookingGet1.getId());
        assertEquals(booking1.getUserId(),  bookingGet1.getUserId());
        assertEquals(booking1.getRoomId() ,  bookingGet1.getRoomId());
        assertEquals(booking1.getStart_date(),  bookingGet1.getStart_date());
        assertEquals(booking1.getEnd_date(), bookingGet1.getEnd_date());

        // test delete
        int sizeBeforeDelete = dbService.getBookings(0,Sort.Direction.ASC).size();
        dbService.deleteBookingById(bookingGet1.getId());
        int sizeAfterDelete = dbService.getBookings(0,Sort.Direction.ASC).size();
        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);

        dbService.deleteUserById(userSaved1.getId());
        dbService.deleteRoomById(roomSaved1.getId());
        dbService.deleteHotelById(hotelSaved1.getId());
        dbService.deleteLocalizationById(localizationSaved1.getId());
    }
}

