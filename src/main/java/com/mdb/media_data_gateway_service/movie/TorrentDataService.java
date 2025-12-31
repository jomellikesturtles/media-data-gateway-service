package com.mdb.media_data_gateway_service.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TorrentDataService {

    private final TorrentRepository torrentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public int saveTorrentFromJson(String jsonContent) {
        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            // Check structure: root -> data -> movies (array)
            JsonNode moviesNode = root.path("data").path("movies");

            if (moviesNode.isMissingNode() || !moviesNode.isArray()) {
                // Fallback if the input is directly the movie object or list
                if (root.has("imdb_code")) {
                    saveTorrent(root);
                    return 1;
                } else if (root.isArray()) {
                    int count = 0;
                    for (JsonNode node : root) {
                        if (node.has("imdb_code")) {
                            saveTorrent(node);
                            count++;
                        }
                    }
                    if (count > 0) {
                        return count;
                    }
                }

                log.warn("No movies found in the provided JSON.");
                return 0;
            }

            int count = 0;
            for (JsonNode movieNode : moviesNode) {
                saveTorrent(movieNode);
                count++;
            }
            return count;

        } catch (IOException e) {
            log.error("Failed to parse JSON", e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    private void saveTorrent(JsonNode movieNode) {
        String imdbCode = movieNode.path("imdb_code").asText();
        if (imdbCode == null || imdbCode.isEmpty()) {
            log.warn("Skipping movie without imdb_code");
            return;
        }

        TorrentEntity entity = new TorrentEntity();
        entity.setImdbId(imdbCode);
        entity.setData(movieNode.toString());
        torrentRepository.save(entity);
    }

    public String getTorrentJson(String imdbId) {
        return torrentRepository.findById(imdbId)
                .map(TorrentEntity::getData)
                .orElse(null);
    }
}
