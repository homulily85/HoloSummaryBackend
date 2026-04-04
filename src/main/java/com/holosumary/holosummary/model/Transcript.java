package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Transcript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @JsonAlias("language_code")
    private String languageCode;

    private String language;

    @JsonAlias("is_generated")
    private boolean isGenerated;
}
