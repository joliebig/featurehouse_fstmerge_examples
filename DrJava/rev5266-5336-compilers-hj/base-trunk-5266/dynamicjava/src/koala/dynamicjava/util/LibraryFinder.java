

package koala.dynamicjava.util;

import java.io.*;
import java.util.*;



public class LibraryFinder extends FileFinder {
  
  private List<String> suffixes;
  
  
  public LibraryFinder() {
    suffixes = new LinkedList<String>();
  }
  
  
  public void addSuffix(String s) {
    suffixes.add(s);
  }
  
  
  public File findCompilationUnit(String cname) throws ClassNotFoundException {
    for (String s : suffixes) {
      String fname = cname.replace('.', '/') + s;
      try {
        return findFile(fname);
      } catch (IOException e) {
      }
      int    n;
      while ((n = fname.lastIndexOf('$')) != - 1) {
        fname = fname.substring(0, n)
          + fname.substring(fname.lastIndexOf('.'), fname.length());
        try {
          return findFile(fname);
        } catch (IOException e) {
        }
      }
    }
    throw new ClassNotFoundException(cname);
  }
  
  
  public String findCompilationUnitName(String cname) throws ClassNotFoundException {
    for (String s : suffixes) {
      String fname = cname.replace('.', '/') + s;
      try {
        findFile(fname);
        return cname;
      } catch (IOException e) {
      }
      int    n;
      while ((n = fname.lastIndexOf('$')) != - 1) {
        fname = fname.substring(0, n)
          + fname.substring(fname.lastIndexOf('.'), fname.length());
        try {
          findFile(fname);
          String result = fname.substring(0, fname.indexOf('.'));
          return result.replace('/', '.');
        } catch (IOException e) {
        }
      }
    }
    throw new ClassNotFoundException(cname);
  }
}
