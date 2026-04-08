package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TranscriptSnippet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    Integer id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "start_time")
    private Double start;

    @Column(name = "end_time")
    private Double end;

    @ManyToOne
    @JoinColumn(name = "transcript_id")
    @JsonIgnore
    private Transcript transcript;
}
