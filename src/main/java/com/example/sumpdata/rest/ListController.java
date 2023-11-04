package com.example.sumpdata.rest;

import com.example.sumpdata.data.DataEntryService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = {"/{year}/{month}", "/{year}", ""})
    public List<String> getListAvailableDayOfMonth(@PathVariable Integer device, @PathVariable(required = false) Integer year, @PathVariable(required = false) Integer month) {
        return dataEntryService.available(device, year, month);
    }

}
