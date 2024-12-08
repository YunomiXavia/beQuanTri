package com.example.beQuanTri.repository.revenue;

import com.example.beQuanTri.entity.revenue.DailyRevenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, String> {
    List<DailyRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate);
    Page<DailyRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}
