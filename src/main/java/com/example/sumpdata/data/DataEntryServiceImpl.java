package com.example.sumpdata.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = {"dataaccess"}, cacheManager = "redisCacheManager")
public class DataEntryServiceImpl implements DataEntryService {
    Pattern CSV_FILENAME_PATTERN = Pattern.compile("waterlevel-([0-9]{4})([0-9]{2})([0-9]{2})\\.csv");

    @Autowired
    private DataEntryRepository dataEntryRepository;

    @Value("${sump.batch.size:1000}")
    private int batchSize;

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
    public DataEntry add(DataEntry entry) {
        // TODO: We may need a sort of validation here.
        return dataEntryRepository.save(entry);
    }

    @Override
    @Cacheable
    public List<DataEntry> retrieveInRange(int deviceId, LocalDateTime start, LocalDateTime end, boolean ascending) {
        Sort sortBy = ascending ? Sort.by("measuredOn").ascending() : Sort.by("measuredOn").descending();
        return dataEntryRepository.findByDeviceIDAndMeasuredOnBetween(deviceId, start, end, sortBy);
    }

    @Override
    public int processCSV(int deviceId, InputStream inputStream, String filename) throws IOException, InvalidCSVFilenameException {
        Matcher matcher = CSV_FILENAME_PATTERN.matcher(filename);
        if (!matcher.find()) {
            throw new InvalidCSVFilenameException("Invalid file name pattern: " + filename);
        }
        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        // This will be a part of date time, like 2023-08-12T00:22:33
        String date = year + "-" + month + "-" + day + "T";

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        int count = 0;
        List<DataEntry> entries = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            DataEntry dataEntry = processOneLine(deviceId, date, line);
            entries.add(dataEntry);
            count++;
            if (entries.size() == batchSize) {
                dataEntryRepository.saveAll(entries);
                entries.clear();
            }
        }
        if (!entries.isEmpty()) {
            dataEntryRepository.saveAll(entries);
        }
        return count;
    }

    private DataEntry processOneLine(int deviceId, String date, String line) {
        String[] values = line.split(",");
        // The CSV format is <time>,<depthInCM>
        String time = values[0];
        String depthInCM = values[1];

        DataEntry dataEntry = new DataEntry();
        dataEntry.setDeviceID(deviceId);
        dataEntry.setMeasuredOn(LocalDateTime.parse(date + time, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        BigDecimal valueInCentiMeter = new BigDecimal(depthInCM);
        dataEntry.setValue(valueInCentiMeter.multiply(BigDecimal.TEN).intValue());
        return dataEntry;
    }

    @Override
    @Cacheable
    public List<String> listAvailability(Integer device, Integer year, Integer month) {
        if (null != month) {
            return dataEntryRepository.availableDateInMonth(device, year, month).stream().map(dt -> dt.replace('-', '/')).toList();
        } else if (null != year) {
            return dataEntryRepository.availableMonthInYear(device, year).stream().map(mo -> "" + year + "/" + mo).toList();
        } else {
            return dataEntryRepository.availableMonthInYear(device);
        }
    }
}
