package com.dgreat.rfid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {

  public static void main(String[] args) {
    String url1 =
      "http://menate.kapotiglobal.com/system/m801_notify.php?oper=notify&tags=E200680D0000000000000001";
    String url2 =
      "https://nims.infinityalps.online:12101/notify_upm/server/home.php?notificationSubmit=1&tag_id=E200680D0000000000000001&msg=New+Pick+Up+For+Tag+ID%3A+E200680D0000000000000001";

    readRedirect(url1);
    //readUrl(url2);
  }

  public static void readUrl(String urlString) {
    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(new URL(urlString).openStream())
      )
    ) {
      StringBuilder buffer = new StringBuilder();
      char[] chars = new char[1024];
      int read;
      while ((read = reader.read(chars)) != -1) {
        buffer.append(chars, 0, read);
      }
      System.out.println("[Test] ==> URL Content:\n" + buffer);
    } catch (Exception e) {
      System.err.println(
        "[Test] ==> Failed to read URL: " + urlString + " - " + e.getMessage()
      );
      e.printStackTrace();
    }
  }

  public static void readRedirect(String urlString) {
    try {
      URL obj = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
      conn.setReadTimeout(5000);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");

      int status = conn.getResponseCode();
      boolean redirect =
        (status == HttpURLConnection.HTTP_MOVED_TEMP ||
          status == HttpURLConnection.HTTP_MOVED_PERM ||
          status == HttpURLConnection.HTTP_SEE_OTHER);

      try (
        BufferedReader in = new BufferedReader(
          new InputStreamReader(conn.getInputStream())
        )
      ) {
        StringBuilder html = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) html.append(line);
        System.out.println("[Test] ==> URL Content:\n" + html);
      }

      System.out.println("[Test] ==> Response Code: " + status);

      if (redirect) {
        String newUrl = conn.getHeaderField("Location");
        System.out.println("[Test] ==> Redirecting to " + newUrl);
        readRedirect(newUrl);
      }
    } catch (Exception e) {
      System.err.println(
        "[Test] ==> Failed to read or redirect URL: " +
        urlString +
        " - " +
        e.getMessage()
      );
      e.printStackTrace();
    }
  }
}
