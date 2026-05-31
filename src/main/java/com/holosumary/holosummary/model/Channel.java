package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Channel {
    @Id
    private String id;

    private String name;

    @JsonAlias("english_name")
    private String englishName;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private String photo;
}
