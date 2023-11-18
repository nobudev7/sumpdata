package com.example.sumpdata;

import com.example.sumpdata.data.DataEntryServiceImpl;
import com.example.sumpdata.rest.ListController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// https://www.javadevjournal.com/spring-boot/spring-rest-docs/

@RunWith(SpringRunner.class)
@WebMvcTest(ListController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataEntryServiceImpl service;

    @Autowired
    private WebApplicationContext context;


    @Test
    public void shouldReturnListOfYears() throws Exception {

        doReturn(List.of("2023")).when(service).available(1, null, null);
        this.mockMvc.perform(get("/devices/1/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\"2023\"]"))
                .andDo(document("list"))
        ;
//                .andDo(document("list",
//                        pathParameters(parameterWithName("id").description("Device id. Required"))));
//
    }

}
