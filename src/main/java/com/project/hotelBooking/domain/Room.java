package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROOMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ROOM_NUMBER")
    private int roomNumber;
    @Column(name = "NUMBER_OF_PERSONS")
    private int numberOfPersons;
    @Column(name = "STANDARD")
    private int standard;
    private Long hotelId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "roomId", updatable = false, insertable = false)
    private List<Booking> bookings;

}
