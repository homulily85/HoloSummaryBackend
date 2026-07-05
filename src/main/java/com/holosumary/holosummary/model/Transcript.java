package com.holosumary.holosummary.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transcripts")
public class Transcript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private String languageCode;

    private String language;

    private boolean isGenerated;
}