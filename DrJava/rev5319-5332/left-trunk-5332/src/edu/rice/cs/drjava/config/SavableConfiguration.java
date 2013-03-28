

package edu.rice.cs.drjava.config;

import java.io.*;
import java.util.Date;


public class SavableConfiguration extends Configuration {
  
  public SavableConfiguration(OptionMap map) { super(map); }

  
  public void loadConfiguration(InputStream is) throws IOException {
    new OptionMapLoader(is).loadInto(map);
  }

  
  public void saveConfiguration(OutputStream os, String header) throws IOException {
    PrintWriter w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
    



    
    
    Date date = new Date();
    w.println("#"+header);
    w.println("#"+date.toString());
    w.print(toString());
    w.close();
    
  }
}
