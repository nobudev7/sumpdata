package com.example.sumpdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.sumpdata.data.DataEntryService;
import com.example.sumpdata.rest.DataEntryController;
import com.example.sumpdata.rest.ListController;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
class SumpdataApplicationTests {

    @Autowired
    private DataEntryController dataEntryController;

    @Autowired
    private ListController listController;

    @Autowired
    private DataEntryService dataEntryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertThat(dataEntryController).isNotNull();
        assertThat(listController).isNotNull();
        assertThat(dataEntryService).isNotNull();
    }


}
