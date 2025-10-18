package com.dgreat.rfid;

import com.rfid.uhf.Device;

public class RFID {

  private static final String DUMP_FILE_PATH = "C:\\ddump.txt";
  private static final int DEFAULT_PORT = 3; // COM3
  private static final byte DEFAULT_BAUD = 7; // 57600bps

  private final Tag tag = new Tag();
  private final byte[] comAddr = new byte[] { (byte) 255 };
  private final int[] portHandle = new int[1];
  private final byte[] powerdBm = new byte[] { 0 };

  public int openComPort() {
    try {
      System.loadLibrary("com_rfid_uhf_Device");
      Device reader = new Device();
      int result = reader.OpenComPort(
        DEFAULT_PORT,
        comAddr,
        DEFAULT_BAUD,
        portHandle
      );
      if (result == 0) {
        reader.SetRfPower(comAddr, powerdBm[0], portHandle[0]);
        System.out.println("[RFID] ==> Port opened successfully");
      } else {
        System.out.println("[RFID] ==> Failed to open port. Code: " + result);
      }
      return result;
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
        "[RFID] ==> Native library not found: " + e.getMessage()
      );
      return -1;
    } catch (Exception e) {
      System.err.println("[RFID] ==> Error opening port: " + e.getMessage());
      return -1;
    }
  }

  public String readInventory() {
    StringBuilder tags = new StringBuilder();
    Device reader = new Device();

    byte QValue = 1;
    byte Session = 0;
    byte MaskMem = 2;
    byte[] MaskAdr = new byte[2];
    byte MaskLen = 0;
    byte[] MaskData = new byte[256];
    byte MaskFlag = 0;
    byte AdrTID = 0;
    byte LenTID = 6;
    byte TIDFlag = 1;
    byte Target = 0;
    byte InAnt = (byte) 0x80;
    byte Scantime = 1;
    byte FastFlag = 0;
    byte[] pEPCList = new byte[20000];
    int[] TotalLen = new int[1];
    int[] CardNum = new int[1];

    int result = reader.Inventory_G2(
      comAddr,
      QValue,
      Session,
      MaskMem,
      MaskAdr,
      MaskLen,
      MaskData,
      MaskFlag,
      AdrTID,
      LenTID,
      TIDFlag,
      Target,
      InAnt,
      Scantime,
      FastFlag,
      pEPCList,
      new byte[1],
      TotalLen,
      CardNum,
      portHandle[0]
    );

    if (result != 0) {
      System.err.println("[RFID] ==> Inventory failed. Code: " + result);
      return "";
    }

    if (CardNum[0] > 0) {
      System.out.println("[RFID] ==> Found " + CardNum[0] + " tag(s)");
      int offset = 0;

      for (int i = 0; i < CardNum[0]; i++) {
        int epcLen = pEPCList[offset++] & 0xFF;
        StringBuilder epcBuilder = new StringBuilder();

        for (int n = 0; n < epcLen; n++) {
          String hex = Integer.toHexString(pEPCList[offset++] & 0xFF);
          if (hex.length() == 1) hex = "0" + hex;
          epcBuilder.append(hex);
        }

        int rssi = pEPCList[offset++];
        String entry = rssi + "|" + epcBuilder.toString().toUpperCase();

        if (tags.length() > 0) tags.append("|");
        tags.append(entry);

        tag.add(
          String.valueOf(rssi),
          epcBuilder.toString().toUpperCase(),
          System.currentTimeMillis(),
          "system"
        );
      }
    } else {
      System.out.println("[RFID] ==> No tags found");
    }

    return tags.toString();
  }

  public int closeComPort() {
    Device reader = new Device();
    int result = reader.CloseSpecComPort(portHandle[0]);
    if (result == 0) {
      System.out.println("[RFID] ==> Port closed successfully");
    } else {
      System.err.println("[RFID] ==> Failed to close port. Code: " + result);
    }
    return result;
  }

  public static void main(String[] args) {
    System.out.println("[RFID] ==> Starting RFID integration");
    RFID rfid = new RFID();
    rfid.openComPort();
    String data = rfid.readInventory();
    System.out.println("[RFID] ==> Tags: " + data);
    rfid.closeComPort();
  }
}
