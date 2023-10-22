package com.example.sumpdata.data;

import java.time.LocalDateTime;
import java.util.List;

public interface DataEntryService {
    public DataEntry add(int deviceId, LocalDateTime measuredOn, String depthInCm);
    public DataEntry add(int deviceId, LocalDateTime measuredOn, int depthInMm);

    public List<DataEntry> retrieveAll();

}
