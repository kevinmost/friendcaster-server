package com.kevinmost.friendcaster_server.api;

import com.kevinmost.friendcaster_server.model.RssJson;
import com.kevinmost.friendcaster_server.model.RssJson.Episode;
import com.kevinmost.friendcaster_server.model.RssXml;
import com.kevinmost.friendcaster_server.util.DurationUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FriendCachester {
    private static final long INVALIDATION_INTERVAL_MILLIS = 5 * 60 * 1000;
    private RssJson cachedEpisodes;

    private long lastUpdatedTimestamp;

    private final FriendcasterApi API = new FriendcasterApi();

    public FriendCachester() {
        refreshCache();
    }

    public RssJson get() {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdatedTimestamp > INVALIDATION_INTERVAL_MILLIS) {
            refreshCache();
        }
        while (cachedEpisodes == null) {
            sleep(250);
        }
        return cachedEpisodes;
    }

    private void refreshCache() {
        API.api.getEpisodes("podcast").enqueue(new Callback<RssXml>() {
            @Override
            public void onResponse(Response<RssXml> response, Retrofit retrofit) {
                final RssJson result = response.body().toJSON();
                DurationUtil.populateDurations(result);
                lastUpdatedTimestamp = System.currentTimeMillis();
                System.err.println("Refreshed cached Friendcast feed");
                cachedEpisodes = result;
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
