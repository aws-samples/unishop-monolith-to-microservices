package com.monoToMicro.Lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Map;

public class Util {

  public static String getCookie(APIGatewayProxyRequestEvent event, String cookieName){

    String cookieHeader = event.getHeaders().get("Cookie");

    if(cookieHeader == null){
      return null;
    }

    String[] cookies = cookieHeader.split("; ?");
    for (String cookie : cookies) {
      int equalsIndex = cookie.indexOf("=");
      String key = cookie.substring(0,equalsIndex);
      if(key.equals(cookieName)){
        String value = cookie.substring(equalsIndex+1);
        return value;
      }
    }

    return null;
  }
}
