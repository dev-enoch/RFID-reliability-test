package com.dgreat.rfid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MyFunctions {

  public static String readUrl(String urlString) {
    StringBuilder content = new StringBuilder();
    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(new URL(urlString).openStream())
      )
    ) {
      char[] buffer = new char[1024];
      int read;
      while ((read = reader.read(buffer)) != -1) {
        content.append(buffer, 0, read);
      }
      System.out.println(
        "[MyFunctions] ==> Successfully read URL: " + urlString
      );
    } catch (Exception e) {
      System.err.println(
        "[MyFunctions] ==> Failed to read URL: " +
        urlString +
        " - " +
        e.getMessage()
      );
      e.printStackTrace();
    }
    return content.toString();
  }
}
