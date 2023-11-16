package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "devices/{device}/entries")
public class DataEntryController {

    // TODO: How do we want to document the Rest API?

    @Autowired
    private DataEntryService dataEntryService;

    @Operation(summary = "Returns data entries for the specified date.")
    @GetMapping("/{year}/{month}/{day}")
    public List<DataEntry> getEntriesByDate(@PathVariable int device, @PathVariable int year, @PathVariable int month, @PathVariable int day,
                                            @RequestParam(required = false, defaultValue = "true") Boolean ascending) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59, 59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    @Operation(summary = "Returns all data entries for the specified month.", description = "This could be a time consuming call as it returns all data entries for a month. " +
            "The normal use case assumes one data entry per minute, that means this endpoint would return 60 x 24 x 30 = 43200 data entries. Use with caution.")
    @GetMapping("/{year}/{month}")
    public List<DataEntry> getEntriesByMonth(@PathVariable int device, @PathVariable int year, @PathVariable int month,
                                             @RequestParam(required = false, defaultValue = "true") boolean ascending) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    // TODO: DataEntry object has deviceID in it. Consider refactoring to avoid a situation where path variable's
    //       device id doesn't agree with one in the DataEntry.
    @Operation(summary = "Upload one data entry as a JSON object.")
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody DataEntry addDataEntryJson(@RequestBody DataEntry entry) {
        return dataEntryService.add(entry);
    }

    @Operation(summary = "Upload one data entry with query parameters", operationId = "addDataEntry", method = "addDataEntry")
    @PostMapping(path = "", consumes = {MediaType.TEXT_PLAIN_VALUE})
    public @ResponseBody DataEntry addDataEntry(@PathVariable Integer device,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime measuredOn,
                                                @RequestParam String value) {
        return dataEntryService.add(device, measuredOn, value);
    }

    /***
     * This endpoint takes multipart file upload.
     * @param device device id
     * @param files file names in waterlevel-yyyyMMdd.csv format. Multiple files are accepted.
     * @return Status message
     */
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> uploadDataEntryFile(@PathVariable Integer device,
            @RequestParam("files") MultipartFile[] files) {
        Map<String, Object> uploadDetails = new HashMap<>();
        uploadDetails.put("numFiles", String.valueOf(files.length));
        List<Map> statusList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Map<String, String> fileStatusMap = new HashMap<>();
            fileStatusMap.put("fileName", files[i].getOriginalFilename());
            try {
                String status = dataEntryService.processCSV(device, files[i].getInputStream(), files[i].getOriginalFilename());
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
