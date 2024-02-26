package com.project.hotelBooking.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "LOCALIZATIONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Localization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CITY")
    private String city;
    @Column(name = "COUNTRY")
    private String country;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "localizationId", updatable = false, insertable = false)
    private List<Hotel> hotels;

}

