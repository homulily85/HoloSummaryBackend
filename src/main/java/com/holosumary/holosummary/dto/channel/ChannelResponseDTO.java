package com.holosumary.holosummary.dto.channel;

import lombok.Data;

@Data
public class ChannelResponseDTO {
    private String id;
    private String name;
    private String englishName;
    private String photo;
    private boolean isFavorite;
    private GroupResponseDTO group;
}