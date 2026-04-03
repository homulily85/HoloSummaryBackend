package com.holosumary.holosummary.client;

import com.holosumary.holosummary.client.dto.HolodexApiResponseDTO;
import com.holosumary.holosummary.model.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Component
public class HolodexVideosApiClient {
    private final RestClient restClient = RestClient.create();
    @Value("${name.holodex-videos-api-client.url}")
    private String url;
    @Value("${name.holodex-videos-api-client.referer}")
    private String referer;
    @Value("${name.holodex-videos-api-client.user-agent}")
    private String userAgent;

    public List<Video> fetchVideoList() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(URI.create(url))
                .queryParam("status", "past,missing,live,upcoming")
                .queryParam("paginated", false)
                .queryParam("max_upcoming_hours", 24)
                .queryParam("org", "Hololive")
                .queryParam("type", "stream")
                .queryParam("limit", 50)
                .queryParam("offset", 0);
        var uri = builder.build().encode().toUri();
        var response = restClient.get()
                .uri(uri)
                .header("Referer", referer)
                .header("User-Agent", userAgent)
                .retrieve()
                .body(new ParameterizedTypeReference<HolodexApiResponseDTO>() {
                });

        return response == null ? Collections.emptyList() : response.getItems();
    }
}
