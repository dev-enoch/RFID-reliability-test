package com.dgreat.rfid;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Tag {

  private final DbCon dbc = new DbCon();

  public boolean add(String rssi, String epc, long times, String added) {
    boolean success = false;
    String logPrefix = "[Tag] ==> ";
    try (
      Connection cn = dbc.connect();
      PreparedStatement ps = cn.prepareStatement(
        "INSERT INTO tags (rssi, epc, times, added) VALUES (?, ?, ?, ?)"
      )
    ) {
      ps.setString(1, rssi);
      ps.setString(2, epc);
      ps.setLong(3, times);
      ps.setString(4, added);

      success = ps.executeUpdate() == 1;
      System.out.println(
        logPrefix + "Added tag " + epc + " successfully: " + success
      );
    } catch (Exception e) {
      System.err.println(
        logPrefix + "Failed to add tag " + epc + ": " + e.getMessage()
      );
      e.printStackTrace();
    }
    return success;
  }

  public static void main(String[] args) {
    Tag t = new Tag();
    System.out.println("[Tag] ==> Adding sample tag");
    boolean result = t.add(
      "100",
      "E2000016591702080740ABCD",
      1,
      "2025-10-18 10:00:00"
    );
    System.out.println("[Tag] ==> Result: " + result);
  }
}
