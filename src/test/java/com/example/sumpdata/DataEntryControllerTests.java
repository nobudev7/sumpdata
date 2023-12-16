package com.example.sumpdata;

import com.example.sumpdata.data.DataEntry;
import com.example.sumpdata.data.DataEntryService;
import com.example.sumpdata.rest.DataEntryController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DataEntryController.class)
public class DataEntryControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataEntryService dataEntryService;

    @Test
    void entryControllerShouldReturnEntries() throws Exception {

        DataEntry entry1 = new DataEntry();
        entry1.setDeviceID(1);
        entry1.setMeasuredOn(LocalDateTime.of(2023, 11, 12, 15, 18, 06));
        entry1.setValue(152);

        DataEntry entry2 = new DataEntry();
        entry2.setDeviceID(1);
        entry2.setMeasuredOn(LocalDateTime.of(2023, 11, 12, 15, 19, 06));
        entry2.setValue(150);

        LocalDateTime start = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 12, 23, 59, 59);
        given(dataEntryService.retrieveInRange(1, start, end, true)).willReturn(List.of(entry1, entry2));

        mvc.perform(get("/devices/{device}/entries/{year}/{month}/{day}", 1, 2023, 11, 12))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("[0].deviceID", is(1)))
                .andExpect(jsonPath("[0].measuredOn", is("2023-11-12T15:18:06")))
                .andExpect(jsonPath("[0].value", is(152)));
    }

    @Test
    void entryControllerShouldReturnEntriesForAMonth() throws Exception {

        DataEntry entry1 = new DataEntry();
        entry1.setDeviceID(1);
        entry1.setMeasuredOn(LocalDateTime.of(2023, 11, 12, 15, 18, 06));
        entry1.setValue(152);

        DataEntry entry2 = new DataEntry();
        entry2.setDeviceID(1);
        entry2.setMeasuredOn(LocalDateTime.of(2023, 11, 12, 15, 19, 06));
        entry2.setValue(150);

        LocalDateTime start = LocalDateTime.of(2023, 11, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 30, 23, 59, 59);
        given(dataEntryService.retrieveInRange(1, start, end, true)).willReturn(List.of(entry1, entry2));

        mvc.perform(get("/devices/{device}/entries/{year}/{month}", 1, 2023, 11))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("[0].deviceID", is(1)))
                .andExpect(jsonPath("[0].measuredOn", is("2023-11-12T15:18:06")))
                .andExpect(jsonPath("[0].value", is(152)));
    }
}
