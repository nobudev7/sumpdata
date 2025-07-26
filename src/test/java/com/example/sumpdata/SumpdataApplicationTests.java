package com.example.sumpdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.sumpdata.rest.DataEntryController;
import com.example.sumpdata.rest.ListController;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.sumpdata.data.DataEntryService;


@WebMvcTest({DataEntryController.class, ListController.class})
class SumpdataApplicationTests {

    @Autowired
    private DataEntryController dataEntryController;

    @Autowired
    private ListController listController;

    @MockBean
    private DataEntryService dataEntryService;


    @Test
    void contextLoads() {
        assertThat(dataEntryController).isNotNull();
        assertThat(listController).isNotNull();
        assertThat(dataEntryService).isNotNull();
    }


}
