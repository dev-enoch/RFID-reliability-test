package com.dgreat.rfid;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Props {

  private final Properties prop = new Properties();
  private final String configFile = "config.properties";

  public Props() {
    String now = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    setProp("LastRun", now);
    System.out.println("[Props] ==> Initialized LastRun to " + now);
  }

  public String getProp(String key) {
    try (FileInputStream input = new FileInputStream(configFile)) {
      prop.load(input);
      String value = prop.getProperty(key);
      if (value != null) {
        System.out.println("[Props] ==> Retrieved " + key + " = " + value);
        return value;
      } else {
        System.out.println("[Props] ==> Property not found: " + key);
      }
    } catch (IOException e) {
      System.err.println(
        "[Props] ==> Failed to read properties: " + e.getMessage()
      );
    }
    return "Not Found";
  }

  public boolean setProp(String key, String value) {
    boolean success = false;
    try (FileInputStream input = new FileInputStream(configFile)) {
      prop.load(input);
    } catch (IOException e) {
      System.out.println(
        "[Props] ==> Properties file not found, creating new."
      );
    }

    prop.setProperty(key, value);

    try (FileOutputStream output = new FileOutputStream(configFile)) {
      prop.store(output, null);
      success = true;
      System.out.println("[Props] ==> Set " + key + " = " + value);
    } catch (IOException e) {
      System.err.println(
        "[Props] ==> Failed to save property " + key + ": " + e.getMessage()
      );
    }

    return success;
  }

  public static void main(String[] args) {
    Props p = new Props();
    System.out.println("[Props] ==> LastRun = " + p.getProp("LastRun"));
    p.setProp("license", "2048");
  }
}
