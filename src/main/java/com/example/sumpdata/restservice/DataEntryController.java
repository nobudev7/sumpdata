package com.example.sumpdata.restservice;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping(path="/rest")
public class DataEntryController {
    @Autowired
    private DataEntryRepository dataEntryRepository;

    @PostMapping(path="/add")
    public @ResponseBody String addDataEntry(
            @RequestParam Integer deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
            @RequestParam Integer value
    ) {
        DataEntry dataEntry = new DataEntry();
        dataEntry.setDeviceID(deviceId);
        dataEntry.setMeasuredOn(measuredOn);
        dataEntry.setValue(value);
        dataEntryRepository.save(dataEntry);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<DataEntry> getAllDataEntries() {
        return dataEntryRepository.findAll();
    }


}
