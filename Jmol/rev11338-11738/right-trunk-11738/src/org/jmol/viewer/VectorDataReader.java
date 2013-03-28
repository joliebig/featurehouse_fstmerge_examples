package org.jmol.viewer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;


public class VectorDataReader extends DataReader {

  private Vector data;
  private int pt;
  private int len;
  
  public VectorDataReader(Vector data) {
    super(new StringReader(""));
    this.data = data;
    len = data.size();
  }

  public int read(char[] buf) throws IOException {
    return readBuf(buf);
  }
  
  public String readLine() {
    return (pt < len ? (String) data.get(pt++) : null);
  }
  
  int ptMark;
  public void mark(long ptr) {
    
    ptMark = pt;
  }
  
  public void reset() {
    pt = ptMark;
  }
}
