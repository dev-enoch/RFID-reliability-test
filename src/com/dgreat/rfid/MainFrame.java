package com.dgreat.rfid;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.BadLocationException;

public class MainFrame {

  private JFrame frame;
  private JTextField txtNotifyUrl;
  private JTextArea resultHere;
  private JTextField baudRate;
  private JTextField portNo;
  private JButton btnStop;
  private JButton btnStart;
  private JButton btnClear;
  private JButton btnSaveSetting;

  private final Props props = new Props();
  private final Tag tag = new Tag();
  private final RFID rfid = new RFID();

  private volatile boolean bStop = false;
  private int open = 55;
  private int readFlag = 0;

  public static void main(String[] args) {
    System.setProperty("java.net.preferIPv4Stack", "true");

    EventQueue.invokeLater(() -> {
      try {
        MainFrame window = new MainFrame();
        window.frame.setVisible(true);
      } catch (Exception e) {
        System.err.println("[Main] ==> " + e.getMessage());
        e.printStackTrace();
      }
    });
  }

  public MainFrame() {
    initialize();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    frame.setTitle("Dgreat CF MU801/804 Integration");
    frame.setLocationRelativeTo(null);

    Image img = Toolkit.getDefaultToolkit()
      .createImage(
        ClassLoader.getSystemResource("com/dgreat/resources/icon.png")
      );
    frame.setIconImage(img);

    // Load properties
    String bd = props.getProp("baud");
    String pt = props.getProp("port");
    String license = props.getProp("license");
    String notify = props.getProp("notify");

    // URL Field
    txtNotifyUrl = new JTextField(
      notify != null && !notify.equals("Not Found")
        ? notify
        : "http://demo.epaxcis.com/system/m801_notify.php"
    );
    txtNotifyUrl.setBounds(10, 90, 315, 35);
    frame.getContentPane().add(txtNotifyUrl);

    JLabel lblServerUrl = new JLabel("Server URL");
    lblServerUrl.setFont(new Font("Tahoma", Font.BOLD, 14));
    lblServerUrl.setBounds(10, 65, 100, 20);
    frame.getContentPane().add(lblServerUrl);

    // Baud Rate Field
    baudRate = new JTextField(bd != null && !bd.equals("Not Found") ? bd : "5");
    baudRate.setBounds(10, 34, 104, 20);
    frame.getContentPane().add(baudRate);
    JLabel lblBaudRate = new JLabel("Baud Rate");
    lblBaudRate.setFont(new Font("Tahoma", Font.BOLD, 14));
    lblBaudRate.setBounds(10, 11, 79, 20);
    frame.getContentPane().add(lblBaudRate);

    // Port Field
    portNo = new JTextField(pt != null && !pt.equals("Not Found") ? pt : "3");
    portNo.setBounds(125, 34, 92, 20);
    frame.getContentPane().add(portNo);
    JLabel lblPortNo = new JLabel("Port No.");
    lblPortNo.setFont(new Font("Tahoma", Font.BOLD, 14));
    lblPortNo.setBounds(125, 11, 79, 20);
    frame.getContentPane().add(lblPortNo);

    // Buttons
    btnStart = new JButton("Start");
    btnStart.setFont(new Font("Tahoma", Font.BOLD, 12));
    btnStart.setBackground(Color.GREEN);
    btnStart.setBounds(236, 33, 89, 23);
    btnStart.addActionListener(this::startAction);
    frame.getContentPane().add(btnStart);

    btnStop = new JButton("Stop");
    btnStop.setFont(new Font("Tahoma", Font.BOLD, 12));
    btnStop.setBackground(Color.RED);
    btnStop.setBounds(335, 33, 89, 23);
    btnStop.addActionListener(this::stopAction);
    btnStop.setEnabled(false);
    frame.getContentPane().add(btnStop);

    btnClear = new JButton("Clear");
    btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
    btnClear.setBackground(Color.BLUE);
    btnClear.setForeground(Color.WHITE);
    btnClear.setBounds(335, 137, 89, 23);
    btnClear.addActionListener(e -> clearOutput());
    frame.getContentPane().add(btnClear);

    btnSaveSetting = new JButton("Save");
    btnSaveSetting.setFont(new Font("Tahoma", Font.BOLD, 14));
    btnSaveSetting.setBounds(335, 90, 89, 35);
    btnSaveSetting.setToolTipText("Save Current Settings as Start Up Default");
    btnSaveSetting.addActionListener(e -> saveSettings());
    frame.getContentPane().add(btnSaveSetting);

    // Output Area
    resultHere = new JTextArea();
    resultHere.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(resultHere);
    scrollPane.setBounds(10, 167, 414, 183);
    frame.getContentPane().add(scrollPane);

    PrintStream printStream = new PrintStream(
      new CustomOutputStream(resultHere)
    );
    System.setOut(printStream);
    System.setErr(printStream);

    // License check
    checkLicense(license);
  }

  private void startAction(ActionEvent e) {
    try {
      bStop = false;
      String url = txtNotifyUrl.getText();
      byte baud = Byte.parseByte(baudRate.getText());
      int port = Integer.parseInt(portNo.getText());

      props.setProp("baud", baudRate.getText());
      props.setProp("port", portNo.getText());
      props.setProp("notify", url);

      open = rfid.openComPort(port, baud);
      getRFID();

      btnStart.setEnabled(false);
      btnStop.setEnabled(true);
      System.out.println("[Main] ==> RFID reader started");
    } catch (Exception ex) {
      System.err.println("[StartAction] ==> " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void stopAction(ActionEvent e) {
    try {
      if (readFlag == 0) {
        rfid.closeComPort();
        bStop = true;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        System.out.println("[Main] ==> RFID reader stopped");
      }
    } catch (Exception ex) {
      System.err.println("[StopAction] ==> " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void clearOutput() {
    try {
      resultHere.getDocument().remove(0, resultHere.getDocument().getLength());
      System.out.println("[Main] ==> Output cleared");
    } catch (BadLocationException ex) {
      System.err.println("[ClearOutput] ==> " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void saveSettings() {
    try {
      props.setProp("baud", baudRate.getText());
      props.setProp("port", portNo.getText());
      props.setProp("notify", txtNotifyUrl.getText());
      System.out.println("[Settings] ==> Settings saved");
    } catch (Exception ex) {
      System.err.println("[SaveSettings] ==> " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void checkLicense(String license) {
    try {
      long now = Instant.now().toEpochMilli();
      String check = license.replaceAll("[a-zA-Z]", "");
      long lic = Long.parseLong(check);

      if (now > lic) {
        JOptionPane.showMessageDialog(frame, "[License] ==> License expired");
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnSaveSetting.setEnabled(false);
        btnClear.setEnabled(false);
      } else {
        System.out.println("[License] ==> License valid");
      }
    } catch (Exception e) {
      System.err.println(
        "[License] ==> Error checking license: " + e.getMessage()
      );
    }
  }

  public void getRFID() {
    new Thread(() -> {
      byte baud = Byte.parseByte(baudRate.getText());
      int port = Integer.parseInt(portNo.getText());
      ArrayList<String> tstamp = new ArrayList<>();
      tstamp.add("0");
      SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

      while (!bStop) {
        try {
          if (open == 0) {
            System.out.println("[RFID] ==> Port open");
            readFlag = 1;
            String tags = rfid.readInventory();
            tstamp.add(formatter.format(new Date()));

            if (tags != null && !tags.isEmpty()) {
              String[] tagArr = tags.replace("|", "&").split("&");
              for (int i = 0; i < tagArr.length; i += 2) {
                try {
                  tag.add(
                    tagArr[i],
                    tagArr[i + 1],
                    1,
                    tstamp.get(tstamp.size() - 1)
                  );
                  System.out.println("[RFID] ==> Card added: " + tagArr[i + 1]);
                } catch (Exception ex) {
                  System.err.println(
                    "[RFID] ==> Failed to add card: " + tagArr[i + 1]
                  );
                }
              }
            }

            readFlag = 0;
          }
          Thread.sleep(5000);
        } catch (Exception ex) {
          System.err.println("[RFID] ==> " + ex.getMessage());
          ex.printStackTrace();
        }
      }
    }).start();
  }

  public static String readUrl(String urlString) throws Exception {
    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(new URL(urlString).openStream())
      )
    ) {
      StringBuilder buffer = new StringBuilder();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
      return buffer.toString();
    }
  }

  public static String readRedirect(String urlString) {
    String ret = "";
    try {
      URL obj = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
      conn.setReadTimeout(5000);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");

      int status = conn.getResponseCode();
      boolean redirect =
        status != HttpURLConnection.HTTP_OK &&
        (status == HttpURLConnection.HTTP_MOVED_TEMP ||
          status == HttpURLConnection.HTTP_MOVED_PERM ||
          status == HttpURLConnection.HTTP_SEE_OTHER);

      try (
        BufferedReader in = new BufferedReader(
          new InputStreamReader(conn.getInputStream())
        )
      ) {
        StringBuilder html = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) html.append(inputLine);
        ret = html.toString();
      }

      if (redirect) {
        String newUrl = conn.getHeaderField("Location");
        ret = readRedirect(newUrl);
        System.out.println("[HTTP] ==> Redirecting to " + newUrl);
      }
    } catch (Exception e) {
      System.err.println("[HTTP] ==> " + e.getMessage());
      e.printStackTrace();
    }
    return ret;
  }
}
