package com.example.sumpdata.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class DataEntryServiceImplTest {

    @InjectMocks
    private DataEntryServiceImpl dataEntryService;

    @Mock
    private DataEntryRepository dataEntryRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dataEntryService, "batchSize", 10);
    }

    @Test
    void processCSVShouldReturnCorrectCountAndDates() throws IOException, InvalidCSVFilenameException {
        // Given
        String filename = "waterlevel-20250811.csv";
        File initialFile = new File("src/test/data/" + filename);
        InputStream inputStream = new FileInputStream(initialFile);
        List<List<DataEntry>> capturedBatches = new ArrayList<>();

        doAnswer(invocation -> {
            List<DataEntry> list = invocation.getArgument(0);
            capturedBatches.add(new ArrayList<>(list)); // Create a copy
            return null;
        }).when(dataEntryRepository).saveAll(any());

        // When
        int processedCount = dataEntryService.processCSV(1, inputStream, filename);

        // Then
        assertEquals(1434, processedCount);

        DataEntry firstEntry = capturedBatches.getFirst().getFirst();
        assertEquals(LocalDateTime.of(2025, 8, 11, 0, 0, 6), firstEntry.getMeasuredOn());

        List<DataEntry> lastBatch = capturedBatches.getLast();
        DataEntry lastEntry = lastBatch.getLast();
        assertEquals(LocalDateTime.of(2025, 8, 11, 23, 59, 7), lastEntry.getMeasuredOn());
    }
}
