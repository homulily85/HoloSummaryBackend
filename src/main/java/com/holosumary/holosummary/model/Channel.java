package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(unique = true, nullable = false)
    private String channelId;

    private String name;

    @JsonAlias("english_name")
    private String englishName;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private String photo;
}