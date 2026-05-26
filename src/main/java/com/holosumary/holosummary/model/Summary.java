package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Summary {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @JsonIgnore
    private Integer promptTokens;

    @JsonIgnore
    private Integer completionTokens;

    @JsonIgnore
    private Integer totalTokens;

    @JsonIgnore
    private String model;
}
