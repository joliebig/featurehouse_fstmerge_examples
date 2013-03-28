package org.jmol.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;



abstract public class DataReader extends BufferedReader {

  public DataReader(Reader in) {
    super(in);
  }

  public BufferedReader getBufferedReader() {
    return this;
  }  

  protected int readBuf(char[] buf) throws IOException {
    
    int nRead = 0;
    String line = readLine();
    if (line == null)
      return 0;
    int linept = 0;
    int linelen = (line == null ? -1 : line.length());
    for (int i = 0; i < buf.length && linelen >= 0; i++) {
        if (linept >= linelen) {
          linept = 0;
          buf[i] = '\n';
          line = readLine();
          linelen = (line == null ? -1 : line.length());
        } else {
          buf[i] = line.charAt(linept++);
        }
        nRead++;
    }
    return nRead;
  }

}
