package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(path="/rest")
public class DataEntryController {

    @Autowired
    private DataEntryService dataEntryService;

    @PostMapping(path="/add")
    public @ResponseBody DataEntry addDataEntry(
            @RequestParam Integer deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
            @RequestParam String value
    ) {
        return dataEntryService.add(deviceId, measuredOn, value);
    }

    @GetMapping(path="/all")
    public @ResponseBody List<DataEntry> getAllDataEntries() {
        return dataEntryService.retrieveAll();
    }


}
