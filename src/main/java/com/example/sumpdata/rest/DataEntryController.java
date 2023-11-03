package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(path = "devices/{device}/entries")
public class DataEntryController {

    // TODO: How do we want to document the Rest API?

    @Autowired
    private DataEntryService dataEntryService;

    @GetMapping("/{year}/{month}/{day}")
    public List<DataEntry> getEntriesByDate(@PathVariable int device, @PathVariable int year, @PathVariable int month, @PathVariable int day,
                                            @RequestParam(required = false, defaultValue = "true") Boolean ascending) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59, 59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    @GetMapping("/{year}/{month}")
    public List<DataEntry> getEntriesByMonth(@PathVariable int device, @PathVariable int year, @PathVariable int month,
                                             @RequestParam(required = false, defaultValue = "true") Boolean ascending) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    // This is different from the above 2 get mappings as it would not return a list of DataEntry objects.
    // This just return the latest 1 DataEntry. The intention is to use it as a starting yyyy/MM/dd value
    // to call the /{year}/{month}/{day} endpoint above.
    @GetMapping(path = "/")
    public DataEntry getLatest(@PathVariable int device) {
        Optional<DataEntry> latest = dataEntryService.latest(device);
        return latest.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified device " + device + " does not have any data entries."));
    }

    // TODO: DataEntry object has deviceID in it. Consider refactoring to avoid a situation where path variable's
    //       device id doesn't agree with one in the DataEntry.
    @PostMapping(path = "/", consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public @ResponseBody DataEntry addDataEntry(@RequestBody DataEntry entry) {
        return dataEntryService.add(entry);
    }

    @PostMapping(path = "/", consumes = {TEXT_PLAIN_VALUE})
    public @ResponseBody DataEntry addDataEntry(@PathVariable Integer deviceID,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
                                                @RequestParam String value) {
        return dataEntryService.add(deviceID, measuredOn, value);
    }

    // TODO: Properly use POST, PUT, GET, DELETE
    // TODO: To get the latest yyyy/mm/dd, create a new endpoint like `/latest`
    // TODO: List device ids


    @RequestMapping(path = "/range")
    public @ResponseBody List<DataEntry> getDataEntriesRange(@RequestParam int deviceID, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                                                             @RequestParam(required = false, defaultValue = "true") boolean ascending) {
        return dataEntryService.retrieveInRange(deviceID, start, end, ascending);
    }


    @PostMapping(path = "/upload")
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
