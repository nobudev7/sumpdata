package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/entries")
public class DataEntryController {

    // TODO: How do we want to document the Rest API?

    @Autowired
    private DataEntryService dataEntryService;

    @GetMapping("/{year}/{month}/{day}")
    public List<DataEntry> getEntriesByDate(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int day,
            @RequestParam int device,
            @RequestParam(required = false, defaultValue = "true") Boolean ascending
    ) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0,0);
        LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59,59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    @GetMapping("/{year}/{month}")
    public List<DataEntry> getEntriesByMonth(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false) Integer device,
            @RequestParam(required = false, defaultValue = "true") Boolean ascending
    ) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59,59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }



    @PostMapping(path="/")
    public @ResponseBody DataEntry addDataEntry(
            @RequestParam Integer deviceID,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
            @RequestParam String value
    ) {
        return dataEntryService.add(deviceID, measuredOn, value);
    }

    // TODO: Make yyyy/mm/dd endpoint
    // TODO: Properly use POST, PUT, GET, DELETE
    // TODO: To get the latest yyyy/mm/dd, create a new endpoint like `/latest`
    // TODO: List device ids


    @GetMapping(path="/latest")
    public @ResponseBody String getLatest(
            @RequestParam(required = false) Integer deviceID
    ) {
        return dataEntryService.latest(deviceID);
    }


    @RequestMapping(path="/range")
    public @ResponseBody List<DataEntry> getDataEntriesRange(
            @RequestParam int deviceID,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false, defaultValue = "true") boolean ascending
    )
    {
        return dataEntryService.retrieveInRange(deviceID, start, end, ascending);
    }


    @PostMapping(path="/upload")
    public ResponseEntity<Map<String, Object>> uploadDataEntryFile(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        Map<String, Object> uploadDetails = new HashMap<>();
        uploadDetails.put("numFiles", String.valueOf(files.length));
        // Consider proper error handling in missing device id, etc.
        int deviceId = Integer.parseInt(request.getHeader("SumpDeviceId"));
        List<Map> statusList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Map<String, String> fileStatusMap = new HashMap<>();
            fileStatusMap.put("fileName", files[i].getOriginalFilename());
            try {
                String status = dataEntryService.processCSV(deviceId, files[i].getInputStream(), files[i].getOriginalFilename());
                // TODO: Consider better status reporting for each file
                fileStatusMap.put("success", status);
            } catch (IOException e) {
                fileStatusMap.put("error", e.getLocalizedMessage());
            }
            statusList.add(fileStatusMap);
        }
        uploadDetails.put("status", statusList);
        return ResponseEntity.ok(uploadDetails);

    }

}
