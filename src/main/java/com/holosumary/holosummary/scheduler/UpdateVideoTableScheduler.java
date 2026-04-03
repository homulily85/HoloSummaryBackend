package com.holosumary.holosummary.scheduler;

import com.holosumary.holosummary.client.HolodexVideosApiClient;
import com.holosumary.holosummary.model.Talent;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.TalentRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpdateVideoTableScheduler {
    private final HolodexVideosApiClient holodexVideosApiClient;
    private final VideoRepository videoRepository;
    private final TalentRepository talentRepository;

    public UpdateVideoTableScheduler(HolodexVideosApiClient holodexVideosApiClient,
                                     VideoRepository videoRepository,
                                     TalentRepository talentRepository) {
        this.holodexVideosApiClient = holodexVideosApiClient;
        this.videoRepository = videoRepository;
        this.talentRepository = talentRepository;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void fetchVideosAndUpdateVideoDatabase() {
        log.info("Cron job ran");

        var fetchedVideos = holodexVideosApiClient.fetchRecentVideos();

        // Holodex API may mistakenly include some independent vtubers so we need to filter them.
        Set<String> talentIds = fetchedVideos.stream()
                .map(Video::getTalent)
                .filter(Objects::nonNull)
                .map(Talent::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Talent> existingTalents = talentRepository.findAllById(talentIds).stream()
                .collect(Collectors.toMap(Talent::getId, t -> t));

        List<Video> toSave = fetchedVideos.stream()
                .filter(v -> {
                    Talent t = v.getTalent();
                    return t == null || existingTalents.containsKey(t.getId());
                })
                .toList();

        videoRepository.saveAll(toSave);
    }
}
