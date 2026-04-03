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
    @Value("${name.holodex-videos-api-client.query.status}")
    private String status;
    @Value("${name.holodex-videos-api-client.query.paginated}")
    private boolean paginated;
    @Value("${name.holodex-videos-api-client.query.max_upcoming_hours}")
    private int maxUpcomingHours;
    @Value("${name.holodex-videos-api-client.query.org}")
    private String org;
    @Value("${name.holodex-videos-api-client.query.type}")
    private String type;
    @Value("${name.holodex-videos-api-client.query.limit}")
    private int limit;
    @Value("${name.holodex-videos-api-client.query.offset}")
    private int offset;

    public List<Video> fetchRecentVideos() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(URI.create(url))
                .queryParam("status", status)
                .queryParam("paginated", paginated)
                .queryParam("max_upcoming_hours", maxUpcomingHours)
                .queryParam("org", org)
                .queryParam("type", type)
                .queryParam("limit", limit)
                .queryParam("offset", offset);
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
