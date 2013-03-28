

package edu.rice.cs.util.jar;

import edu.rice.cs.util.UnexpectedException;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.io.*;


public class ManifestWriter {
  private List<String> _classPaths;
  private String _mainClass;
  public static final Manifest DEFAULT = new ManifestWriter().getManifest();

  
  public ManifestWriter() {
    _classPaths = new LinkedList<String>();
    _mainClass = null;
  }

  
  public void addClassPath(String path) {
    _classPaths.add(_classPaths.size(), path);
  }

  
  public void setMainClass(String mainClass) {
    _mainClass = mainClass;
  }

  
  protected InputStream getInputStream() {
    

    StringBuffer sbuf = new StringBuffer();
    sbuf.append(Attributes.Name.MANIFEST_VERSION.toString());
    sbuf.append(": 1.0"+System.getProperty("line.separator"));
    if( !_classPaths.isEmpty() ) {
      Iterator<String> iter = _classPaths.iterator();
      sbuf.append(Attributes.Name.CLASS_PATH.toString());
      sbuf.append(":");
      while (iter.hasNext()) {
        sbuf.append(" ");
        sbuf.append(iter.next());
      }
      sbuf.append(System.getProperty("line.separator"));
    }
    if( _mainClass != null ) {
      sbuf.append(Attributes.Name.MAIN_CLASS.toString());
      sbuf.append(": ");
      sbuf.append(_mainClass);
      sbuf.append(System.getProperty("line.separator"));
    }
    try {
      return new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8"));
    }
    catch (UnsupportedEncodingException e) {
      throw new UnexpectedException(e);
    }
  }

  
  public Manifest getManifest() {
    try {
      Manifest m = new Manifest();
      m.read(getInputStream());
      return m;
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}