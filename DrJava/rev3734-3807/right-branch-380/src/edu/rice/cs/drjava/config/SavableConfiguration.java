

package edu.rice.cs.drjava.config;

import java.io.*;
import java.util.Iterator;
import java.util.Date;


public class SavableConfiguration extends Configuration {
  
  public SavableConfiguration(OptionMap map) { super(map); }

  
  public void loadConfiguration(InputStream is) throws IOException {
    new OptionMapLoader(is).loadInto(map);
  }

  
  public void saveConfiguration(OutputStream os, String header) throws IOException {
    OutputStreamWriter osw = new OutputStreamWriter(os);
    Iterator<OptionParser<?>> keys = map.keys();
    
    String tmpString;
    StringBuffer buff;
    OptionParser<?> key;
    
    
    Date date = new Date();
    osw.write((int)'#');
    osw.write(header, 0, header.length());
    osw.write((int)'\n');
    osw.write((int)'#');
    osw.write(date.toString(), 0, date.toString().length());
    osw.write((int)'\n');

    
    while (keys.hasNext()) {
      key = keys.next();

      if (!key.getDefault().equals(map.getOption(key))) {

        
        tmpString = key.getName();
        osw.write(tmpString, 0, tmpString.length());

        
        tmpString = " = ";
        osw.write(tmpString, 0, 3);

        
        tmpString = map.getString(key);
        
        int index = 0;
        int pos;
        while (index < tmpString.length() &&
               ((pos = tmpString.indexOf('\\', index)) >= 0)) {
          buff = new StringBuffer(tmpString);
          buff.insert(pos, '\\');
          index = pos + 2;
          tmpString = buff.toString();
        }
        osw.write(tmpString, 0, tmpString.length());
        osw.write((int)'\n');

        
      }
    }
    osw.close();
    
  }
}
