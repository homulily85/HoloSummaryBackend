package com.holosumary.holosummary.dto;

import lombok.Data;

import java.util.List;

@Data
public class HolodexApiResponseDTO {
    private int total;

    private List<VideoDTO> items;
}
