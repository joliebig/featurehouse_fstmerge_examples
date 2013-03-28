

package koala.dynamicjava.util;

import java.io.*;
import java.util.*;



public class FileFinder {
  
  private List<String> paths;
  
  
  public FileFinder() {
    paths = new LinkedList<String>();
  }
  
  
  public void addPath(String path) {
    String s = (path.endsWith("/")) ? path : path + "/";
    paths.remove(s);
    paths.add(0, s);
  }
  
  
  public File findFile(String name) throws IOException {
    for (String s : paths) {
      File f = new File(s + name);
      if (f.exists()) {
        return f;
      }
    }
    throw new IOException("File Not Found: " + name);
  }
}
