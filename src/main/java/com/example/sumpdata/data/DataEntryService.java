package com.example.sumpdata.data;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface DataEntryService {
    DataEntry add(int deviceId, LocalDateTime measuredOn, String depthInCm);

    DataEntry add(int deviceId, LocalDateTime measuredOn, int depthInMm);

    DataEntry add(DataEntry entry);

    List<DataEntry> retrieveAll(Integer deviceID);

    List<DataEntry> retrieveInRange(int deviceId, LocalDateTime start, LocalDateTime end, boolean ascending);

    String processCSV(int deviceId, InputStream stream, String filename) throws IOException;

    String latest(Integer deviceID);
}
