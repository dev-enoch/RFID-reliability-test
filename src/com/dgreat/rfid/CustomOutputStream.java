package com.dgreat.rfid;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 * Redirects output stream to a JTextArea.
 */
public class CustomOutputStream extends OutputStream {

  private final JTextArea textArea;

  public CustomOutputStream(JTextArea textArea) {
    this.textArea = textArea;
  }

  @Override
  public void write(int b) throws IOException {
    // Redirect single character to text area
    textArea.append(String.valueOf((char) b));
    // Scroll to the end
    textArea.setCaretPosition(textArea.getDocument().getLength());
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    // Optional override for efficiency when writing byte arrays
    String text = new String(b, off, len);
    textArea.append(text);
    textArea.setCaretPosition(textArea.getDocument().getLength());
  }
}
