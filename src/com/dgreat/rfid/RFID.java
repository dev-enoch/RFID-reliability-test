package com.dgreat.rfid;

import java.io.Console;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class RFID {

  String path = "C:\\ddump.txt";
  Tag tag = new Tag();
  public int Port = 3; //com3
  public byte[] comAddr = new byte[1];
  public byte baud = 7; //57600bps
  public int[] PortHandle = new int[1];

  byte[] versionInfo = new byte[2];
  byte[] readerType = new byte[1];
  byte[] trType = new byte[1];
  byte[] dmaxfre = new byte[1];
  byte[] dminfre = new byte[1];
  byte[] powerdBm = new byte[1];
  //byte[]powerdBm= {0};//new byte[1];
  //byte[]powerdBm= {9};//Enable for with antenna;
  byte[] InventoryScanTime = new byte[1];
  byte[] Ant = new byte[1];
  byte[] BeepEn = new byte[1];
  byte[] OutputRep = new byte[1];
  byte[] CheckAnt = new byte[1];

  public int openComPort(int port, byte baud) {
    System.loadLibrary("com_rfid_uhf_Device");
    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    comAddr[0] = (byte) 255;

    int result = reader.OpenComPort(port, comAddr, baud, PortHandle);
    //int sp = reader.SetRfPower(comAddr, powerdBm[0], PortHandle[0]);
    //System.out.println(baud+"-"+port+"Open Port "+ result + "Power " + sp);

    return result;
  }

  public int openComPort() {
    System.loadLibrary("com_rfid_uhf_Device");
    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    comAddr[0] = (byte) 255;

    int result = reader.OpenComPort(Port, comAddr, baud, PortHandle);
    reader.SetRfPower(comAddr, powerdBm[0], PortHandle[0]);
    //int sp = System.out.println("Open Port "+ result + "Power " + sp);
    //System.out.println("Open Port "+ result);

    return result;
  }

  public String readInventory(int port, byte baud) {
    String tags = "";

    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    byte QValue = 1;
    byte Session = 0;
    byte MaskMem = 2;
    byte[] MaskAdr = new byte[2];
    byte MaskLen = 0;
    byte[] MaskData = new byte[256];
    byte MaskFlag = 0;
    byte AdrTID = 0;
    byte LenTID = 6;
    byte TIDFlag = 1; //��TID��ǰ6����
    byte Target = 0;
    byte InAnt = (byte) 0x80;
    byte Scantime = 1;
    byte FastFlag = 0;
    byte[] pEPCList = new byte[20000];
    int[] Totallen = new int[1];
    int[] CardNum = new int[1];
    //System.out.println("Port Handle "+PortHandle[0]);
    //System.out.println("Power "+powerdBm[0]);
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
      Ant,
      Totallen,
      CardNum,
      PortHandle[0]
    );
    //System.out.println("Inventory_G2 "+result);
    //System.out.println("Cardnums "+CardNum[0]);

    System.out.println(result);

    if (CardNum[0] > 0) {
      //System.out.println("We have "+CardNum[0]+" card(s)");

      int m = 0;
      for (int index = 0; index < CardNum[0]; index++) {
        int epclen = pEPCList[m++] & 255;
        String EPCstr = "";
        byte[] epc = new byte[epclen];
        for (int n = 0; n < epclen; n++) {
          byte bbt = pEPCList[m++];
          epc[n] = bbt;
          String hex = Integer.toHexString(bbt & 255);
          if (hex.length() == 1) {
            hex = "0" + hex;
          }
          EPCstr += hex;
        }

        int rssi = pEPCList[m++];
        //   			 tag.add(EPCstr.toUpperCase());
        //if(tags == "") {tags = EPCstr.toUpperCase();}
        //else {tags = tags+"|"+EPCstr.toUpperCase();}
        //System.out.println(index+". "+EPCstr.toUpperCase());
      }
    }

    return tags;
  }

  public String readInventory() {
    String tags = "";

    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    byte QValue = 1;
    byte Session = 0;
    byte MaskMem = 2;
    byte[] MaskAdr = new byte[2];
    byte MaskLen = 0;
    byte[] MaskData = new byte[256];
    byte MaskFlag = 0;
    byte AdrTID = 0;
    byte LenTID = 6;
    byte TIDFlag = 1; //��TID��ǰ6����
    byte Target = 0;
    byte InAnt = (byte) 0x80;
    byte Scantime = 1;
    byte FastFlag = 0;
    byte[] pEPCList = new byte[20000];
    int[] Totallen = new int[1];
    int[] CardNum = new int[1];
    //System.out.println("Power "+powerdBm[0]);
    //System.out.println("Port Handle "+PortHandle[0]);

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
      Ant,
      Totallen,
      CardNum,
      PortHandle[0]
    );

    //int i=0;
    //for ( i=0; i<15; i++) System.out.println(pEPCList[i]);

    //	System.out.println("Inventory_G2 "+result);
    //	System.out.println("Cardnums "+CardNum[0]);
    if (CardNum[0] > 0) {
      //System.out.println("We have "+CardNum[0]+" card(s)");
      int m = 0;
      for (int index = 0; index < CardNum[0]; index++) {
        int epclen = pEPCList[m++] & 255;
        String EPCstr = "";
        byte[] epc = new byte[epclen];
        for (int n = 0; n < epclen; n++) {
          byte bbt = pEPCList[m++];
          epc[n] = bbt;
          String hex = Integer.toHexString(bbt & 255);
          if (hex.length() == 1) {
            hex = "0" + hex;
          }
          EPCstr += hex;
        }
        int rssi = pEPCList[m++];
        //		 System.out.println(rssi);

        //tag.add(EPCstr.toUpperCase());
        if (tags == "") {
          tags = rssi + "|" + EPCstr.toUpperCase();
        } else {
          tags = tags + "|" + rssi + "|" + EPCstr.toUpperCase();
        }
        //System.out.println(index+". "+EPCstr.toUpperCase());
      }
    }

    return tags;
  }

  public int closeComPort() {
    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    int result = reader.CloseSpecComPort(PortHandle[0]);
    System.out.println("Close Port " + result);

    return result;
  }

  public int closeComPort(int port, byte baud) {
    com.rfid.uhf.Device reader = new com.rfid.uhf.Device();
    int result = reader.CloseSpecComPort(PortHandle[0]);
    System.out.println("Close Port " + result);

    return result;
  }

  public static void main(String[] args) {
    System.out.println("RFID Integration System");
    RFID rfid = new RFID();
    rfid.openComPort();
    System.out.println(rfid.readInventory());
    rfid.closeComPort();
  }
}
