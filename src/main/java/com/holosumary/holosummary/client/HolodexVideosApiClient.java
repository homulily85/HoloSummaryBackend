package com.holosumary.holosummary.client;

import com.holosumary.holosummary.dto.holodex.HolodexApiResponseDTO;
import com.holosumary.holosummary.dto.holodex.VideoDTO;
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
    private final RestClient restClient;
    @Value("${holodex-videos-api-client.url}")
    private String url;
    @Value("${holodex-videos-api-client.header.referer}")
    private String referer;
    @Value("${holodex-videos-api-client.header.user-agent}")
    private String userAgent;
    @Value("${holodex-videos-api-client.query.status}")
    private String status;
    @Value("${holodex-videos-api-client.query.paginated}")
    private boolean paginated;
    @Value("${holodex-videos-api-client.query.max_upcoming_hours}")
    private int maxUpcomingHours;
    @Value("${holodex-videos-api-client.query.org}")
    private String org;
    @Value("${holodex-videos-api-client.query.type}")
    private String type;
    @Value("${holodex-videos-api-client.query.limit}")
    private int limit;
    @Value("${holodex-videos-api-client.query.offset}")
    private int offset;
    @Value("${holodex-videos-api-client.query.include}")
    private String include;

    public HolodexVideosApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<VideoDTO> fetchRecentVideos() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(URI.create(url))
                .queryParam("status", status)
                .queryParam("paginated", paginated)
                .queryParam("max_upcoming_hours", maxUpcomingHours)
                .queryParam("org", org)
                .queryParam("type", type)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .queryParam("include", include);
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
