package com.kevinmost.friendcaster_server;

import com.google.gson.Gson;
import com.kevinmost.friendcaster_server.api.FriendCachester;

import org.json.JSONArray;
import org.json.JSONObject;

import spark.*;

public class FriendcasterServer {
  private static final Gson GSON = new Gson();
  private static final FriendCachester FRIENDCASTER_CACHE = new FriendCachester();

  public static void main(String[] args) {
    setSparkPort();
    Spark.get("/episodes", (request, response) -> {
      final String pageQueryParam = request.queryParams("page");
      final JSONArray items = FRIENDCASTER_CACHE.get();
      try {
        final int page = Integer.parseInt(pageQueryParam);
        return addHasNextToJSONArray(items, 10 * page,
            Math.min(10 * (page + 1), items.length() - 1));
      } catch (NumberFormatException ignored) {
        return addHasNextToJSONArray(items);
      }
    }, GSON::toJson);
  }

  private static JSONObject addHasNextToJSONArray(JSONArray array) {
    return addHasNextToJSONArray(array, 0, array.length());
  }

  private static JSONObject addHasNextToJSONArray(JSONArray array, int from, int to) {
    final JSONObject object = new JSONObject();
    final JSONArray subArray = jsonArraySubList(array, from, to);
    object.put("episodes", subArray);
    object.put("hasNext", to < array.length() - 1);
    return object;
  }

  private static JSONArray jsonArraySubList(JSONArray array, int from, int to) {
    final JSONArray result = new JSONArray();
    while (from < to) {
      result.put(array.get(from));
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
