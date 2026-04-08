package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Transcript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @JsonAlias("language_code")
    private String languageCode;

    private String language;

    @JsonAlias("is_generated")
    private boolean isGenerated;

    @OneToMany(mappedBy = "transcript", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TranscriptSnippet> snippets = new ArrayList<>();

    public void addSnippet(TranscriptSnippet snippet) {
        snippets.add(snippet);
        snippet.setTranscript(this);
    }
}