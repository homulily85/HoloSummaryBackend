package com.holosumary.holosummary.client.dto;

import com.holosumary.holosummary.model.Video;
import lombok.Data;

import java.util.List;

@Data
public class HolodexApiResponseDTO {
    private int total;

    private List<Video> items;
}
