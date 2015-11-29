package com.kevinmost.friendcaster_server;

import com.google.gson.Gson;
import com.kevinmost.friendcaster_server.api.FriendCachester;
import com.kevinmost.friendcaster_server.model.RssJson;
import com.kevinmost.friendcaster_server.model.RssJson.Episode;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;

public class FriendcasterServer {
  private static final Gson GSON = new Gson();
  private static final FriendCachester FRIENDCASTER_CACHE = new FriendCachester();

  public static void main(String[] args) {
    setSparkPort();
    Spark.get("/episodes", (request, response) -> {
      final String pageQueryParam = request.queryParams("page");
      final RssJson episodes = FRIENDCASTER_CACHE.get();
      try {
        final int page = Integer.parseInt(pageQueryParam);
        return setHasNext(episodes, 10 * page,
                Math.min(10 * (page + 1), episodes.episodeList.size() - 1));
      } catch (NumberFormatException ignored) {
        return setHasNext(episodes);
      }
    }, GSON::toJson);
  }

  private static RssJson setHasNext(RssJson json) {
    return setHasNext(json, 0, json.episodeList.size());
  }

  private static RssJson setHasNext(RssJson json, int from, int to) {
    final RssJson outJson = new RssJson();
    final List<Episode> subList = jsonArraySubList(json.episodeList, from, to);
    outJson.episodeList = subList;
    outJson.hasNext =  to < json.episodeList.size() - 1;
    return outJson;
  }

  private static List<Episode> jsonArraySubList(List<Episode> episodeList, int from, int to) {
    final List<Episode> result = new ArrayList<>();
    while (from < to) {
      result.add(episodeList.get(from));
      from++;
    }
    return result;
  }

  private static void setSparkPort() {
    final String port = EnvironmentVariable.PORT.get();
    try {
      Spark.port(Integer.parseInt(port));
    } catch (NumberFormatException ignored) {
      // Port will stay at default of 4567
    }
  }
}
