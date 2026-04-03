package com.holosumary.holosummary.model;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    private String photo;
}
