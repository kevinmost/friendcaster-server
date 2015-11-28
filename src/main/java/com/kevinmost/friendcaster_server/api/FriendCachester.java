package com.kevinmost.friendcaster_server.api;

import com.kevinmost.friendcaster_server.util.DurationUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FriendCachester {
  private static final long INVALIDATION_INTERVAL_MILLIS = 5 * 60 * 1000;
  private JSONArray cachedEpisodes;

  private long lastUpdatedTimestamp;

  private final FriendcasterAPI API = new FriendcasterAPI();

  public FriendCachester() {
    refreshCache();
  }

  public JSONArray get() {
    final long currentTime = System.currentTimeMillis();
    if (currentTime - lastUpdatedTimestamp > INVALIDATION_INTERVAL_MILLIS) {
      refreshCache();
    }
    return cachedEpisodes;
  }

  private void refreshCache() {
    API.api.getEpisodes("podcast").enqueue(new Callback<JSONObject>() {
      @Override
      public void onResponse(Response<JSONObject> response, Retrofit retrofit) {
        final JSONObject inner = get(response.body(), "rss", "channel");
        cachedEpisodes = inner.getJSONArray("item");
        for (int i = 0; i < cachedEpisodes.length(); i++) {
          final JSONObject thisEpisode = ((JSONObject)cachedEpisodes.get(i));
          final String mp3String = getMP3URLString(thisEpisode);
          thisEpisode.put("duration", DurationUtil.getDurationSeconds(mp3String));
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

  private static String getMP3URLString(JSONObject object) {
    for (String key : object.keySet()) {
      final Object value = object.get(key);
      if (value instanceof JSONObject) {
        return getMP3URLString(((JSONObject)value));
      }
      if (value instanceof String) {
        if (((String)value).endsWith("mp3")) {
          return (String)value;
        }
      }
    }
    return "";
  }

  private static JSONObject get(JSONObject root, String... keys) {
    JSONObject current = root;
    for (String key : keys) {
      current = ((JSONObject)current.get(key));
    }
    return current;
  }
}
