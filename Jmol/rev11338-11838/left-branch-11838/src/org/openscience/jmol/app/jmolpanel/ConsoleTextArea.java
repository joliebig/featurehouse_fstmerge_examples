
package org.openscience.jmol.app.jmolpanel;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.text.Document;
import org.jmol.i18n.GT;

public class ConsoleTextArea extends JTextArea {

  public ConsoleTextArea(InputStream[] inStreams) {
    for (int i = 0; i < inStreams.length; ++i) {
      startConsoleReaderThread(inStreams[i]);
    }
  }    


  public ConsoleTextArea(boolean doRedirect) throws IOException {

    final LoopedStreams ls = new LoopedStreams();

    String redirect = (doRedirect ? System.getProperty("JmolConsole") : "false");
    if (redirect == null || redirect.equals("true")) {
        
        
        PrintStream ps = new PrintStream(ls.getOutputStream());
        System.setOut(ps);
        System.setErr(ps);
    }

    startConsoleReaderThread(ls.getInputStream());
  }    


  private void startConsoleReaderThread(InputStream inStream) {

    final BufferedReader br =
      new BufferedReader(new InputStreamReader(inStream));
    new Thread(new Runnable() {

      public void run() {
        Thread.currentThread().setName("ConsoleReaderThread");
        StringBuffer sb = new StringBuffer();
        try {
          String s;
          Document doc = getDocument();
          s = br.readLine();
          while (s != null) {
            boolean caretAtEnd = false;
            caretAtEnd = (getCaretPosition() == doc.getLength());
            sb.setLength(0);
            append(sb.append(s).append('\n').toString());
            if (caretAtEnd) {
              setCaretPosition(doc.getLength());
            }
            s = br.readLine();
          }
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, GT._(
              "Error reading from BufferedReader: {0}", e.getMessage()));
          System.exit(1);
        }
      }
    }).start();
  }
}
