package com.kevinmost.friendcaster_server.api;

import com.kevinmost.friendcaster_server.model.RssXml;
import com.squareup.okhttp.*;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.SimpleXmlConverterFactory;
import retrofit.http.GET;
import retrofit.http.Query;

class FriendcasterApi {
    public final SBFCService api;

    public FriendcasterApi() {
        final String friendcastURL = "http://superbestfriendsplay.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(friendcastURL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        api = retrofit.create(SBFCService.class);
    }

    public interface SBFCService {
        @GET("/")
        Call<RssXml> getEpisodes(@Query("feed") String feed);
    }
}


