package com.kevinmost.friendcaster_server.model;

import java.util.ArrayList;
import java.util.List;

public class RssJson {
    public boolean hasNext;
    public List<Episode> episodeList = new ArrayList<>();

    public static class Episode {
        public String episodeLink;
        public String title;
        public String date;
        public long duration;
        public String mp3Link;
        public String mimeType;
    }
}
