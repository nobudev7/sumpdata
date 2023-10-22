package com.example.sumpdata.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataEntryRepository extends JpaRepository<DataEntry, DataEntryId> {
    List<DataEntry> findByDeviceID(Integer deviceID);

}
