package com.kevinmost.friendcaster_server.api;

import com.kevinmost.friendcaster_server.retrofit.BestXMLConverterFactory;
import com.squareup.okhttp.*;

import org.json.JSONObject;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

class FriendcasterAPI {
  public final SBFCService api;

  public FriendcasterAPI() {
    final String friendcastURL = "http://superbestfriendsplay.com/";

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(friendcastURL)
        .addConverterFactory(new BestXMLConverterFactory())
        .client(new OkHttpClient())
        .build();

    api = retrofit.create(SBFCService.class);
  }

  public interface SBFCService {
    @GET("/")
    Call<JSONObject> getEpisodes(@Query("feed") String feed);
  }
}


