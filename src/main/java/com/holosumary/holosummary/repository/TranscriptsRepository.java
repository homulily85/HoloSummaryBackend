package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranscriptsRepository extends JpaRepository<Transcript, Integer> {
    List<Transcript> findByVideoAndLanguageCode(Video video, String languageCode);
}
