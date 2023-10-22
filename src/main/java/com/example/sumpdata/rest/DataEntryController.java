package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path="/rest")
public class DataEntryController {

    // TODO: How do we want to document the Rest API?

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
