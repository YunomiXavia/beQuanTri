package com.example.beQuanTri.entity.revenue;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[daily_revenue]")
public class DailyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDate date;
    double totalRevenue;
}
