package com.example.sumpdata;

import com.example.sumpdata.data.DataEntryService;
import com.example.sumpdata.rest.ListController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ListController.class)
public class ListControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataEntryService dataEntryService;

    @Test
    void listControllerShouldReturnYears() throws Exception {

        List<String> listDevice1 = List.of("2023");
        given(dataEntryService.listAvailability(1, null, null)).willReturn(listDevice1);

        mvc.perform(get("/devices/{device}/list", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void listControllerShouldReturnYearMonth() throws Exception {
        List<String> listDevice1Year2023 = List.of("2023/1", "2023/8", "2023/10", "2023/11");
        given(dataEntryService.listAvailability(1, 2023, null)).willReturn(listDevice1Year2023);

        mvc.perform(get("/devices/{device}/list/{year}", 1, 2023))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("[0]", is("2023/1")));
    }

    @Test
    void listControllerShouldReturnYearMonthDay() throws Exception {
        List<String> listDevice1Year2023Month10 = List.of("2023/10/20", "2023/10/21", "2023/10/23");
        given(dataEntryService.listAvailability(1, 2023, 10)).willReturn(listDevice1Year2023Month10);

        mvc.perform(get("/devices/{device}/list/{year}/{month}", 1, 2023, 10))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("[0]", is("2023/10/20")));
    }
}
