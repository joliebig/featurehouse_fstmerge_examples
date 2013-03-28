

package edu.rice.cs.util;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.Vector;


public class ClassPathVector extends Vector<URL> {
  
  public ClassPathVector() { }
  
  public ClassPathVector(int capacity) { super(capacity); }
  
  public String toString() {
    StringBuffer cp = new StringBuffer();
    for(URL u : this) {
      cp.append(formatURL(u));
      cp.append(File.pathSeparator);
    }
    return cp.toString();
  }
  
  
  public void add(String entry) {
    try {
      this.add(new URL(entry));
    } catch(MalformedURLException e) {



    }
  }
  
  
  public void add(File entry) {
    try {
      this.add(entry.toURL());
    } catch(MalformedURLException e) {



    }
  }
  
  public Vector<File> asFileVector() {
    Vector<File> v = new Vector<File>();
    for(URL url : this) { v.add(new File(url.getFile())); }
    return v;
  }
  
  private String formatURL(URL url) { return new File(url.getFile()).toString(); }
}
