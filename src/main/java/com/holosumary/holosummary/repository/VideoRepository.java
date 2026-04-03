package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface VideoRepository extends JpaRepository<Video, String> {
    Page<Video> findAll(Pageable pageable);

    Page<Video> findByStatus(String status, Pageable pageable);

    Page<Video> findByAvailableAtAfter(OffsetDateTime availableAtAfter, Pageable pageable);

}
