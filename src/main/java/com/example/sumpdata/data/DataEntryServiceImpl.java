package com.example.sumpdata.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public List<DataEntry> retrieveAll() {
        // TODO: The plan is to have either paged results, or upper limit in number of entries to return.
        // Also, when we provide a way to set query conditions such as oder by measured on, this will need
        // to be improved.
        List<DataEntry> dataEntries = new ArrayList<>();
        dataEntryRepository.findAll().forEach(dataEntries::add);
        return dataEntries;
    }
}
