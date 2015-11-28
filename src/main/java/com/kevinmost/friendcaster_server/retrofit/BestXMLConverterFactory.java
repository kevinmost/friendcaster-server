package com.kevinmost.friendcaster_server.retrofit;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Scanner;

import retrofit.Converter;
import retrofit.SimpleXmlConverterFactory;

public class BestXMLConverterFactory extends Converter.Factory {
  private final SimpleXmlConverterFactory simple = SimpleXmlConverterFactory.create();

  @Override
  public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
    // Delegate to the simple converter, we have no need for custom behavior
    return simple.toRequestBody(type, annotations);
  }

  @Override
  public Converter<ResponseBody, JSONObject> fromResponseBody(Type type, Annotation[] annotations) {
    return new CustomMappingConverter();
  }

  static class CustomMappingConverter implements Converter<ResponseBody, JSONObject> {
    @Override
    public JSONObject convert(ResponseBody responseBody) throws IOException {
      final String input = convertStreamToString(responseBody.byteStream());
      return XML.toJSONObject(input);
    }

    private static String convertStreamToString(InputStream is) {
      final Scanner s = new Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
    }
  }

}
