package com.example.sumpdata.data;

import org.springframework.data.repository.CrudRepository;

public interface DataEntryRepository extends CrudRepository<DataEntry, DataEntryId> {
}
