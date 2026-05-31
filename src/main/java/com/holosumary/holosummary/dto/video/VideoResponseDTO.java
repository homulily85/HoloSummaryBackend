package com.holosumary.holosummary.dto.video;

import com.holosumary.holosummary.dto.channel.ChannelResponseDTO;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class VideoResponseDTO {
    private String id;
    private String title;
    private String type;
    private String topic;
    private int duration;
    private String status;
    private OffsetDateTime availableAt;
    private ChannelResponseDTO channel;
    private List<ChannelResponseDTO> mentions;
}