package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, String> {
    Page<Video> findAll(Pageable pageable);

    Optional<Video> findById(String id);

    Page<Video> findByStatus(String status, Pageable pageable);

    Page<Video> findVideoByAvailableAtIsBetween(OffsetDateTime availableAtAfter,
                                                OffsetDateTime availableAtBefore,
                                                Pageable pageable);

    Page<Video> findByStatusAndAvailableAtBetween(String status,
                                                  OffsetDateTime availableAtAfter,
                                                  OffsetDateTime availableAtBefore,
                                                  Pageable pageable);
}
