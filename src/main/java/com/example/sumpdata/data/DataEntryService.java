package com.example.sumpdata.data;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface DataEntryService {
    public DataEntry add(int deviceId, LocalDateTime measuredOn, String depthInCm);
    public DataEntry add(int deviceId, LocalDateTime measuredOn, int depthInMm);

    public List<DataEntry> retrieveAll(Integer deviceID);

    List<DataEntry> retrieveInRange(int deviceId, LocalDateTime start, LocalDateTime end, boolean ascending);

    public String processCSV(int deviceId, InputStream stream, String filename) throws IOException;

}