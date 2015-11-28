package com.kevinmost.friendcaster_server.api;

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
        lastUpdatedTimestamp = System.currentTimeMillis();
        System.err.println("Refreshed cached Friendcast feed");
      }

      @Override
      public void onFailure(Throwable throwable) {
        System.err.println(throwable.getMessage());
      }
    });
  }

  private static JSONObject get(JSONObject root, String... keys) {
    JSONObject current = root;
    for (String key : keys) {
      current = ((JSONObject)current.get(key));
    }
    return current;
  }
}
