package com.example.sumpdata.restservice;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path="/rest")
public class DataEntryController {
    @Autowired
    private DataEntryRepository dataEntryRepository;

    @PostMapping(path="/add")
    public @ResponseBody DataEntry addDataEntry(
            @RequestParam Integer deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
            @RequestParam String value
    ) {
        DataEntry dataEntry = new DataEntry();
        dataEntry.setDeviceID(deviceId);
        dataEntry.setMeasuredOn(measuredOn);
        BigDecimal valueInCentiMeter = new BigDecimal(value);
        dataEntry.setValue(valueInCentiMeter.multiply(BigDecimal.TEN).intValue());
        dataEntryRepository.save(dataEntry);
        return dataEntry;
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<DataEntry> getAllDataEntries() {
        return dataEntryRepository.findAll();
    }


}
