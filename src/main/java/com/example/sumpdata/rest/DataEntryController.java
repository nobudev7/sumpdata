package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

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
                                             @RequestParam(required = false, defaultValue = "true") boolean ascending) {
        // Convert the path variables into range of LocalDateTime
        LocalDateTime start = LocalDateTime.of(year, month, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59);
        return dataEntryService.retrieveInRange(device, start, end, ascending);
    }

    // This is different from the above 2 get mappings as it would not return a list of DataEntry objects.
    // This just return the latest 1 DataEntry. The intention is to use it as a starting yyyy/MM/dd value
    // to call the /{year}/{month}/{day} endpoint above.
    @GetMapping(path = "")
    public DataEntry getLatest(@PathVariable int device, @RequestParam(required = false, defaultValue = "false") boolean ascending) {
        Optional<DataEntry> latest = dataEntryService.getEntry(device, ascending);
        return latest.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified device " + device + " does not have any data entries."));
    }

    // TODO: DataEntry object has deviceID in it. Consider refactoring to avoid a situation where path variable's
    //       device id doesn't agree with one in the DataEntry.
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody DataEntry addDataEntry(@RequestBody DataEntry entry) {
        return dataEntryService.add(entry);
    }

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
