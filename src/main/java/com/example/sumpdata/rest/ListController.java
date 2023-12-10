package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "devices/{device}/list")
public class ListController {

    @Autowired
    private DataEntryService dataEntryService;

    @Operation(summary = "List of years that data is available", description = "Returns list of years.")
    @GetMapping(value = "")
    @Cacheable(value = "ListControllerCache")
    public List<String> getListAvailableDayOfMonth(@PathVariable Integer device) {
        return dataEntryService.listAvailability(device, null, null);
    }

    @Operation(summary = "List year/month that data is available", description = "Returns list of year/month that data is available for the specified year.")
    @GetMapping(value = "/{year}")
    @Cacheable(value = "ListControllerCache")
    public List<String> getListAvailableDayOfMonth(@PathVariable Integer device, @PathVariable Integer year) {
        return dataEntryService.listAvailability(device, year, null);
    }

    @Operation(summary = "List available year/month/date", description = "Returns list of year/month/date that data is available for the specified year and month.")
    @GetMapping(value = "/{year}/{month}")
    @Cacheable(value = "ListControllerCache")
    public List<String> getListAvailableDayOfMonth(@PathVariable Integer device, @PathVariable Integer year, @PathVariable Integer month) {
        return dataEntryService.listAvailability(device, year, month);
    }

}
