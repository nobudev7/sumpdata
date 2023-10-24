package com.example.sumpdata.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DataEntryRepository extends JpaRepository<DataEntry, DataEntryId> {
    // Custom queries
    List<DataEntry> findByDeviceID(Integer deviceID);
    List<DataEntry> findByDeviceIDAndMeasuredOnBetween(Integer deviceID, LocalDateTime measuredOnStart, LocalDateTime measuredOnEnd, Sort sort);

}
