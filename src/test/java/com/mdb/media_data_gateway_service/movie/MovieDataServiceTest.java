package com.mdb.media_data_gateway_service.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieDataServiceTest {

    @Mock
    private TorrentRepository torrentRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TorrentDataService movieDataService;

    @Test
    void saveMoviesFromJson_shouldSaveMoviesFromSampleFile() throws Exception {
        // Read the actual sample file
        // Assumes running from project root
        Path path = Paths.get("src/main/java/com/mdb/media_data_gateway_service/stream/sample-response.json");
        String jsonContent = Files.readString(path);

        int count = movieDataService.saveTorrentFromJson(jsonContent);

        assertEquals(1, count); // The sample file has 1 movie

        ArgumentCaptor<TorrentEntity> captor = ArgumentCaptor.forClass(TorrentEntity.class);
        verify(torrentRepository, times(1)).save(captor.capture());

        TorrentEntity saved = captor.getValue();
        assertEquals("tt2015381", saved.getImdbId());
        assertNotNull(saved.getData());
    }
}