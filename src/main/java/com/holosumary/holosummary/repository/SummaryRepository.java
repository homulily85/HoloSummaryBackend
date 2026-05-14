package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Integer> {
    Summary getSummariesByVideo(Video video);
}
