package com.project.hotelBooking.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "HOTELS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
     Long id;
    @Column(name="Name")
    String name;
    @Column(name="NUMBER_OF_STARS")
    int numberOfStars;
    @Column(name="HOTEL_CHAIN")
    String hotelChain;
    Long localizationId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotelId", updatable = false, insertable = false)
    List<Room> rooms;
}
