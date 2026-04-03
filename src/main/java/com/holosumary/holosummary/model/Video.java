package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
public class Video {
    @Id
    private String id;

    private String title;

    private String type;

    @JsonAlias("topic_id")
    private String topic;

    private int duration;

    private String status;

    @JsonAlias("available_at")
    private OffsetDateTime availableAt;

    @ManyToOne
    @JoinColumn(name = "talent_id")
    @JsonAlias("channel")
    private Talent talent;

}
