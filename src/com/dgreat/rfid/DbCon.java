package com.dgreat.rfid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbCon {

  private static final String DB_URL =
    "jdbc:mysql://127.0.0.1:3306/rfid_xp?useTimezone=true&serverTimezone=UTC";
  private static final String DB_USER = "root";
  private static final String DB_PASS = "";

  public DbCon() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("[DbCon] ==> MySQL driver loaded");
    } catch (ClassNotFoundException e) {
      System.err.println(
        "[DbCon] ==> MySQL driver not found: " + e.getMessage()
      );
    }
  }

  public Connection connect() {
    try {
      Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
      System.out.println("[DbCon] ==> Connection established");
      return conn;
    } catch (SQLException e) {
      System.err.println("[DbCon] ==> Connection failed: " + e.getMessage());
      return null;
    }
  }

  public static void main(String[] args) {
    DbCon dbCon = new DbCon();
    try (Connection conn = dbCon.connect()) {
      if (conn != null) {
        System.out.println("[DbCon] ==> Connection test successful");
      } else {
        System.out.println("[DbCon] ==> Connection test failed");
      }
    } catch (SQLException e) {
      System.err.println(
        "[DbCon] ==> Error closing connection: " + e.getMessage()
      );
    }
  }
}
