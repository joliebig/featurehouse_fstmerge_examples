

package edu.rice.cs.util;

import java.io.*;
import java.net.URI;


public class AbsRelFile extends File {
  protected boolean _keepAbsolute = false;

  public AbsRelFile(File parent, String child, boolean keepAbsolute) {
    super(parent, child);
    _keepAbsolute = keepAbsolute;
  }
  public AbsRelFile(File parent, String child) {
    this(parent, child, false);
  }
  public AbsRelFile(String pathname, boolean keepAbsolute) {
    super(pathname);
    _keepAbsolute = keepAbsolute;
  }
  public AbsRelFile(String pathname) {
    this(pathname, false);
  }
  public AbsRelFile(String parent, String child, boolean keepAbsolute) {
    super(parent, child);
    _keepAbsolute = keepAbsolute;
  }
  public AbsRelFile(String parent, String child) {
    this(parent, child, false);
  }
  public AbsRelFile(URI uri, boolean keepAbsolute) {
    super(uri);
    _keepAbsolute = keepAbsolute;
  }
  public AbsRelFile(URI uri) {
    this(uri, false);
  }
  public AbsRelFile(File f, boolean keepAbsolute) {
    this(f.getParent(), f.getName(), keepAbsolute);
  }
  public AbsRelFile(File f) {
    this(f, false);
  }
  public boolean keepAbsolute() { return _keepAbsolute; }
  public AbsRelFile keepAbsolute(boolean keepAbsolute) {
    _keepAbsolute = keepAbsolute;
    return this;
  }
}
