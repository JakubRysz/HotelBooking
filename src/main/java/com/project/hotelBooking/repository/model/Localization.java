package com.project.hotelBooking.repository.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Table(name = "LOCALIZATIONS")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
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

