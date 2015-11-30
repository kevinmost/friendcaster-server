package com.kevinmost.friendcaster_server.util;

import com.kevinmost.friendcaster_server.model.RssJson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;

import javax.sound.sampled.*;

public class DurationUtil {
    public static void populateDurations(RssJson json) {
        json.episodeList.parallelStream()
                .forEach(episode -> {
                    episode.duration = getDurationSeconds(episode.mp3Link);
                });
    }

    private static long getDurationSeconds(String urlString) {
        try {
            final AudioFileFormat stream = AudioSystem.getAudioFileFormat(new URL(urlString));
            final Object lengthTag = stream.properties().get("mp3.id3tag.length");
            if (lengthTag == null) {
                // TODO: we need to actually figure out a length here somehow if they don't report it in the ID3 tags
                return -1L;
            }
            return Long.parseLong(String.valueOf(lengthTag)) / 1000L;
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}