package com.holosumary.holosummary.scheduler;

import com.holosumary.holosummary.client.HolodexVideosApiClient;
import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.ChannelRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpdateVideoTableScheduler {
    private final HolodexVideosApiClient holodexVideosApiClient;
    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;

    public UpdateVideoTableScheduler(HolodexVideosApiClient holodexVideosApiClient,
                                     VideoRepository videoRepository,
                                     ChannelRepository channelRepository) {
        this.holodexVideosApiClient = holodexVideosApiClient;
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void fetchVideosAndUpdateVideoDatabase() {

        log.info("Fetching videos from Holodex API.");

        var fetch_start = System.currentTimeMillis();
        var fetchedVideos = holodexVideosApiClient.fetchRecentVideos();
        var fetch_end = System.currentTimeMillis();

        log.info("Finished fetching videos from Holodex API. Time used %d ms"
                .formatted(fetch_end - fetch_start));

        if (fetchedVideos == null || fetchedVideos.isEmpty()) {
            log.info("No video is fetched.");
            return;
        }

        log.info("Started updating video database.");

        var update_start = System.currentTimeMillis();

        // Filter for non-holo channel
        Map<String, Channel> existingChannels =
                channelRepository.findAll().stream()
                .collect(Collectors.toMap(Channel::getChannelId, t -> t));

        List<Video> toSave = fetchedVideos.stream()
                .map(v -> {
                            var t = v.getChannel();
                            if (t == null || t.getId() == null) {
                                return null;
                            }
                            var channel =
                                    existingChannels.getOrDefault(t.getId(),
                                            null);
                            if (channel == null) {
                                return null;
                            }

                            var video = new Video();
                            video.setVideoId(v.getId());
                            video.setTitle(v.getTitle());
                            video.setType(v.getType());
                            video.setTopic(v.getTopic());
                            video.setDuration(v.getDuration());
                            video.setStatus(v.getStatus());
                            video.setAvailableAt(v.getAvailableAt());
                            video.setChannel(channel);
                            if (v.getMentions() == null || v.getMentions().isEmpty()) {
                                video.setMentions(List.of());
                            } else {
                                video.setMentions(v.getMentions().stream()
                                        .map(m -> existingChannels.getOrDefault(m.getId(), null))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()));
                            }
                            return video;
                        }
                )
                .filter(Objects::nonNull).toList();

        videoRepository.saveAll(toSave);

        var update_end = System.currentTimeMillis();
        log.info("Finished updating video database. Time used %d ms"
                .formatted(update_end - update_start));
    }
}
