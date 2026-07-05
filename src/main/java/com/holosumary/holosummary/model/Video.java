package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(unique = true, nullable = false)
    private String videoId;

    private String title;

    private String type;

    @JsonAlias("topic_id")
    private String topic;

    private int duration;

    private String status;

    @JsonAlias("available_at")
    private OffsetDateTime availableAt;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToMany
    @JoinTable(
            name = "video_mentions",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private List<Channel> mentions;

}
