package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptsRepository extends JpaRepository<Transcript, Integer> {
    Transcript findByVideoAndLanguageCode(Video video, String languageCode);
}
