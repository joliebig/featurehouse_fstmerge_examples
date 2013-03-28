package org.jmol.viewer;

import java.io.IOException;
import java.io.StringReader;


public class ArrayDataReader extends DataReader {
  private String[] data;
  private int pt;
  private int len;
  
  public ArrayDataReader(String[] data) {
    super(new StringReader(""));
    this.data = data;
    len = data.length;
  }

  public int read(char[] buf) throws IOException {
    return readBuf(buf);
  }
    
  public String readLine() {
    return (pt < len ? data[pt++] : null);
  }
  
  int ptMark;
  public void mark(long ptr) {
    
    ptMark = pt;
  }
  
  public void reset() {
    pt = ptMark;
  }
}
