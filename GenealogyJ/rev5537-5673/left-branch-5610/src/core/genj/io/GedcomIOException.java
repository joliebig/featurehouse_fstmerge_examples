
package genj.io;

import java.io.IOException;



public class GedcomIOException extends IOException {

  
  private int line;

  
  public GedcomIOException(String msg, int line) {
    super(msg);
    this.line=line;
  }

  
  public int getLine() {
    return line;
  }          
}
