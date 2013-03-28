

package edu.rice.cs.drjava.config;

import java.io.*;
import java.util.Date;


public class SavableConfiguration extends Configuration {
  
  public SavableConfiguration(OptionMap map) { super(map); }

  
  public void loadConfiguration(InputStream is) throws IOException {
    new OptionMapLoader(is).loadInto(map);
  }

  
  public void saveConfiguration(OutputStream os, String header) throws IOException {
    Writer w = new BufferedWriter(new OutputStreamWriter(os));
    



    
    
    Date date = new Date();
    w.write('#');
    w.write(header, 0, header.length());
    w.write('\n');
    w.write('#');
    w.write(date.toString(), 0, date.toString().length());
    w.write('\n');

    
    for (OptionParser<?> key : map.keys()) {

      if (!key.getDefault().equals(map.getOption(key))) {

        
        String tmpString = key.getName();
        w.write(tmpString, 0, tmpString.length());

        
        tmpString = " = ";
        w.write(tmpString, 0, 3);

        
        tmpString = map.getString(key);
        
        int index = 0;
        int pos;
        while (index < tmpString.length() &&
               ((pos = tmpString.indexOf('\\', index)) >= 0)) {
          final StringBuilder buff = new StringBuilder(tmpString);  
          buff.insert(pos, '\\');
          index = pos + 2;
          tmpString = buff.toString();
        }
        w.write(tmpString, 0, tmpString.length());
        w.write('\n');

        
      }
    }
    w.close();
    
  }
}
