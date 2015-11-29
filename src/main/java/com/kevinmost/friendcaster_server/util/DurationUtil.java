package com.kevinmost.friendcaster_server.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.*;

public class DurationUtil {
    public static long getDurationSeconds(String urlString) {
        try {
            final AudioFileFormat stream = AudioSystem.getAudioFileFormat(new URL(urlString));
            return Long.parseLong(String.valueOf(stream.properties().get("mp3.id3tag.length"))) / 1000L;
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}