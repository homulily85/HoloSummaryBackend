package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Talent {
    @Id
    private String id;

    private String name;

    @JsonAlias("english_name")
    private String englishName;

    @JsonIgnore
    private String groupName;

    private String photo;
}
