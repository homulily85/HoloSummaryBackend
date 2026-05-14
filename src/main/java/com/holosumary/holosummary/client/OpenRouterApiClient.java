package com.holosumary.holosummary.client;

import com.holosumary.holosummary.dto.openrouter.*;
import com.holosumary.holosummary.model.Transcript;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenRouterApiClient {
    private final RestClient restClient;
    @Value("${open-router-api-client.url}")
    private String url;
    @Value("${open-router-api-client.header.authorization}")
    private String authorization;
    @Value("${open-router-api-client.body.model}")
    private String model;
    @Value("${open-router-api-client.body.system-prompt}")
    private String systemPrompt;

    public OpenRouterApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    private TranscriptDTO createTranscriptDTO(Transcript transcript) {
        var speakerName = new ArrayList<String>();
        speakerName.add(transcript.getVideo().getChannel().getEnglishName());
        for (var channel : transcript.getVideo().getMentions()) {
            speakerName.add(channel.getEnglishName());
        }
        return new TranscriptDTO(transcript.getText(), speakerName);
    }

    public OpenRouterResponseDTO fetchSummary(Transcript transcript) {
        var uriComponentsBuilder = UriComponentsBuilder.fromUri(URI.create(url));

        var uri = uriComponentsBuilder.build().encode().toUri();

        return restClient.post()
                .uri(uri)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .body(new OpenRouterRequestDTO(model, List.of(
                        new MessageDTO("system", systemPrompt),
                        new MessageDTO("user", createTranscriptDTO(transcript).toString())
                ), List.of(new ToolDTO("openrouter:web_fetch"), new ToolDTO("openrouter" +
                        ":web_search"))))
                .retrieve()
                .body(OpenRouterResponseDTO.class);
    }
}
