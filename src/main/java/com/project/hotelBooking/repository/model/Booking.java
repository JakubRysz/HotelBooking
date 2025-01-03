package com.project.hotelBooking.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "BOOKINGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    private Long userId;

    private Long roomId;
    @Column(name="START_DATE")
    private LocalDate startDate;
    @Column(name="END_DATE")
    private LocalDate endDate;
}
