package com.project.hotelBooking.domain;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "HOTELS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;
    @Column(name="Name")
    private String name;
    @Column(name="NUMBER_OF_STARS")
    private int numberOfStars;
    @Column(name="HOTEL_CHAIN")
    private String hotelChain;
    Long localizationId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotelId", updatable = false, insertable = false)
    private List<Room> rooms;
}
