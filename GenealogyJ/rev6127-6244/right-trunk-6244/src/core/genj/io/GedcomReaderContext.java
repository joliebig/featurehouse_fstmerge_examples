
package genj.io;

import genj.gedcom.Context;


public interface GedcomReaderContext {

  
  public String getPassword();

  
  public void handleWarning(int line, String warning, Context context);
  
}
