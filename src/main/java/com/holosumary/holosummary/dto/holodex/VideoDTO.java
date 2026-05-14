package com.holosumary.holosummary.dto.holodex;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class VideoDTO {
    private String id;

    private String title;

    private String type;

    @JsonAlias("topic_id")
    private String topic;

    private int duration;

    private String status;

    @JsonAlias("available_at")
    private OffsetDateTime availableAt;

    private ChannelDTO channel;

    private List<ChannelDTO> mentions;
}
