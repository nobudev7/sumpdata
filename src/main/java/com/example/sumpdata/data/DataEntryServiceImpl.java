package com.example.sumpdata.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DataEntryServiceImpl implements DataEntryService{
    @Autowired
    private DataEntryRepository dataEntryRepository;

    @Override
    public DataEntry add(int deviceId, LocalDateTime measuredOn, String depthInCm) {
        BigDecimal valueInCentiMeter = new BigDecimal(depthInCm);
        return add(deviceId, measuredOn, valueInCentiMeter.multiply(BigDecimal.TEN).intValue());
    }

    @Override
    public DataEntry add(int deviceId, LocalDateTime measuredOn, int depthInMm) {
        DataEntry dataEntry = new DataEntry();
        dataEntry.setDeviceID(deviceId);
        dataEntry.setMeasuredOn(measuredOn);
        dataEntry.setValue(depthInMm);
        return dataEntryRepository.save(dataEntry);
    }

    @Override
    public List<DataEntry> retrieveAll(Integer deviceID) {
        // TODO: The plan is to have either paged results, or upper limit in number of entries to return.
        // Also, when we provide a way to set query conditions such as oder by measured on, this will need
        // to be improved.
        List<DataEntry> dataEntries = new ArrayList<>();
        if (null == deviceID) {
            dataEntryRepository.findAll().forEach(dataEntries::add);
        } else {
            dataEntryRepository.findByDeviceID(deviceID).forEach(dataEntries::add);
        }
        return dataEntries;
    }

    @Override
    public List<DataEntry> retrieveInRange(int deviceId, LocalDateTime start, LocalDateTime end, boolean ascending) {
        Sort sortBy = ascending ? Sort.by("measuredOn").ascending() : Sort.by("measuredOn").descending();
        return dataEntryRepository.findByDeviceIDAndMeasuredOnBetween(deviceId, start, end, sortBy);
    }

    Pattern CSV_FILENAME_PATTERN = Pattern.compile("waterlevel-([0-9]{4})([0-9]{2})([0-9]{2})\\.csv");
    @Override
    public String processCSV(int deviceId, InputStream inputStream, String filename) throws IOException {
        Matcher matcher = CSV_FILENAME_PATTERN.matcher(filename);
        if (!matcher.find()) {
            return "Invalid file name pattern: " + filename;
        }
        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        // This will be a part of date time, like 2023-08-12T00:22:33
        String date = year + "-" + month + "-" + day + "T";

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            processOneLine(deviceId, date, line);
            count++;
        }
        return "Processed " + count + " entries for deviceID " + deviceId;
    }

    private void processOneLine(int deviceId, String date, String line) {
        String[] values = line.split(",");
        // The CSV format is <time>,<depthInCM>
        String time = values[0];
        String depthInCM = values[1];

        LocalDateTime measuredOn = LocalDateTime.parse(date + time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        add(deviceId, measuredOn, depthInCM);
    }

    @Override
    public String latest(Integer deviceID) {
        List<DataEntry> entries = dataEntryRepository.findFirstByDeviceIDOrderByMeasuredOnDesc(deviceID);
        if (!entries.isEmpty()) {
            DataEntry latest = entries.getFirst();
            return latest.getMeasuredOn().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        // TODO: Consider to implement better response for an error case
        return "No data for device id = " + deviceID;
    }
}
