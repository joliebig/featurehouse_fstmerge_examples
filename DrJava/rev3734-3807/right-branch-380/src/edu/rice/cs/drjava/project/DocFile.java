

package edu.rice.cs.drjava.project;

import java.io.File;
import java.io.IOException;

import edu.rice.cs.util.Pair;

public class DocFile extends File {
  
  private Pair<Integer,Integer> _sel;
  private Pair<Integer,Integer> _scroll;
  private boolean _active;
  private String _package;
  private long _mod;
  
  
  public DocFile(File f) { this(f, null, null, false, null); }
  
  
  public DocFile(String pathname) { this(pathname, null, null, false, null); }
  
  
  public DocFile(String parent, String child) { this(parent, child, null, null, false, null); }
  
  public DocFile(String pathname, Pair<Integer,Integer> selection, Pair<Integer,Integer> scroll, boolean active, String srcRoot) {
    super(pathname);
    init(selection, scroll, active, srcRoot);
  }
  public DocFile(File f, Pair<Integer,Integer> selection, Pair<Integer,Integer> scroll, boolean active, String srcRoot) {
    super(f, "");
    init(selection, scroll, active, srcRoot);
  }
  
  public DocFile(String parent, String child, Pair<Integer,Integer> selection, Pair<Integer,Integer> scroll, boolean active, String srcRoot) {
    super(parent, child);
    init(selection, scroll, active, srcRoot);
  }
  
  private void init(Pair<Integer,Integer> selection, Pair<Integer,Integer> scroll, boolean active, String pack) {
    _sel = selection;
    _scroll = scroll;
    _active = active;
    _package = pack;
  }
  
  
  
  public DocFile getAbsoluteFile() {
    if (isAbsolute()) return this;
    else
      return new DocFile(super.getAbsoluteFile(), _sel, _scroll, _active, _package);
  }
  
  public DocFile getCanonicalFile() throws IOException {
    return new DocFile(super.getCanonicalFile(), _sel, _scroll, _active, _package);
  }
  
  
  
  public Pair<Integer,Integer> getSelection() { return _sel; }
  
  
  public void setSelection(Pair<Integer,Integer> sel) { _sel = sel; }
  
  
  public void setSelection(int start, int end) {
    _sel = new Pair<Integer,Integer>(new Integer(start), new Integer(end));
  }  
  
  public Pair<Integer,Integer> getScroll() { return _scroll; }
  
  
  public void setScroll(Pair<Integer,Integer> scroll) { _scroll = scroll; }
  
  
  public void setScroll(int vert, int horiz) {
    _scroll = new Pair<Integer,Integer>(new Integer(vert), new Integer(horiz));
  }
  
  
  public boolean isActive() { return _active; }
  
  
  
  public void setActive(boolean active) { _active = active; }
  
  
  public String getPackage() { return _package; }
  
  
  public void setPackage(String pkg) { _package = pkg; }
  
  
  public void setSavedModDate(long mod) { _mod = mod; }
  
  
  public long getSavedModDate() { return _mod; }
}