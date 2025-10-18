package com.dgreat.rfid;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class LicenseFrame {

  private JFrame frame;

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        LicenseFrame window = new LicenseFrame();
        window.frame.setVisible(true);
        System.out.println("[LicenseFrame] ==> Window launched");
      } catch (Exception e) {
        System.err.println(
          "[LicenseFrame] ==> Failed to launch window: " + e.getMessage()
        );
        e.printStackTrace();
      }
    });
  }

  public LicenseFrame() {
    initialize();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setTitle("Dgreat CF MU801/804 Integration");
    frame.setBounds(100, 100, 450, 400);
    frame.setSize(450, 400);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);

    try {
      java.net.URL url = ClassLoader.getSystemResource(
        "com/dgreat/resources/icon.png"
      );
      if (url != null) {
        Image img = Toolkit.getDefaultToolkit().createImage(url);
        frame.setIconImage(img);
        System.out.println("[LicenseFrame] ==> Icon loaded successfully");
      } else {
        System.out.println("[LicenseFrame] ==> Icon resource not found");
      }
    } catch (Exception e) {
      System.err.println(
        "[LicenseFrame] ==> Failed to load icon: " + e.getMessage()
      );
      e.printStackTrace();
    }
  }
}
