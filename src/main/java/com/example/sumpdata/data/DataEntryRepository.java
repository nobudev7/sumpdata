package com.example.sumpdata.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DataEntryRepository extends JpaRepository<DataEntry, DataEntryId> {
    // Custom queries
    List<DataEntry> findByDeviceID(Integer deviceID);
    List<DataEntry> findByDeviceIDAndMeasuredOnBetween(Integer deviceID, LocalDateTime measuredOnStart, LocalDateTime measuredOnEnd, Sort sort);
    List<DataEntry> findFirstByDeviceIDOrderByMeasuredOnDesc(Integer deviceID);

    @Query("SELECT distinct(date(de.measuredOn)) dt from DataEntry as de where de.deviceID=?1 and year(de.measuredOn) = ?2 and month(de.measuredOn) = ?3  order by date(de.measuredOn)")
    List<String> availableDateInMonth(Integer deviceID, Integer year, Integer month);
    @Query("SELECT distinct(month(de.measuredOn)) dt from DataEntry as de where de.deviceID=?1 and year(de.measuredOn) = ?2 order by month(de.measuredOn)")
    List<String> availableMonthInYear(Integer device, Integer year);
    @Query("SELECT distinct(year(de.measuredOn)) dt from DataEntry as de where de.deviceID=?1 order by year(de.measuredOn)")
    List<String> availableMonthInYear(Integer device);
}
