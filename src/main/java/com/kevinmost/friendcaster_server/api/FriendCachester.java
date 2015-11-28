package com.kevinmost.friendcaster_server.api;

import com.kevinmost.friendcaster_server.model.RssJson;
import com.kevinmost.friendcaster_server.model.RssJson.Episode;
import com.kevinmost.friendcaster_server.model.RssXml;
import com.kevinmost.friendcaster_server.model.RssXml.Item;
import com.kevinmost.friendcaster_server.util.DurationUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

public class FriendCachester {
  private static final long INVALIDATION_INTERVAL_MILLIS = 5 * 60 * 1000;
  private RssJson cachedEpisodes;

  private long lastUpdatedTimestamp;

  private final FriendcasterAPI API = new FriendcasterAPI();

  public FriendCachester() {
    refreshCache();
  }

  public RssJson get() {
    final long currentTime = System.currentTimeMillis();
    if (currentTime - lastUpdatedTimestamp > INVALIDATION_INTERVAL_MILLIS) {
      refreshCache();
    }
    return cachedEpisodes;
  }

  private void refreshCache() {
    API.api.getEpisodes("podcast").enqueue(new Callback<RssXml>() {
      @Override
      public void onResponse(Response<RssXml> response, Retrofit retrofit) {
        final RssJson rssJson = xmlToJson(response.body());
        cachedEpisodes = rssJson;
        for (Episode episode : cachedEpisodes.episodeList) {
          episode.duration = DurationUtil.getDurationSeconds(episode.mp3Link);
        }
        lastUpdatedTimestamp = System.currentTimeMillis();
        System.err.println("Refreshed cached Friendcast feed");
      }

      @Override
      public void onFailure(Throwable throwable) {
        System.err.println(throwable.getMessage());
      }
    });
  }

  private static RssJson xmlToJson(RssXml xml) {
    RssJson json = new RssJson();
    for(Item item : xml.channel.item) {
      RssJson.Episode episode = new Episode();

      episode.episodeLink = item.link;
      episode.title = item.title;
      episode.date = item.pubDate;
      episode.mp3Link = item.enclosure.url;
      episode.mimeType = item.enclosure.type;

      json.episodeList.add(episode);
    }
    return json;
  }
}
