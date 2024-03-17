package com.project.hotelBooking.repository.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROOMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
