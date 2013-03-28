

package edu.rice.cs.drjava.config;

import edu.rice.cs.util.FileOps;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.io.IOException;
import edu.rice.cs.util.StringOps;


public abstract class FileListProperty extends DrJavaProperty {
  
  protected String _sep;
  
  protected String _dir;
  
  public FileListProperty(String name, String sep, String dir, String help) {
    super(name, help);
    _sep = sep;
    _dir = dir;
    resetAttributes();
  }
  
  
  public void invalidate() {
    
    invalidateOthers(new HashSet<DrJavaProperty>());
  }
  
  
  public boolean isCurrent() { return false; }

  
  protected abstract List<File> getList(PropertyMaps pm);
  
  
  public void update(PropertyMaps pm) {
    String quot = "";
    String q = _attributes.get("squote");
    if (q != null) {
      if (q.toLowerCase().equals("true")) { quot = "'"; }
    }
    q = _attributes.get("dquote");
    if (q != null) {
      if (q.toLowerCase().equals("true")) { quot = "\"" + quot; }
    }
    List<File> l = getList(pm);
    if (l.size() == 0) { _value = ""; return; }
    StringBuilder sb = new StringBuilder();
    for(File fil: l) {
      sb.append(StringOps.replaceVariables(_attributes.get("sep"), pm, PropertyMaps.GET_CURRENT));
      try {
        String f = fil.toString();
        if (_attributes.get("rel").equals("/")) f = fil.getAbsolutePath();
        else {
          File rf = new File(StringOps.
                               unescapeFileName(StringOps.replaceVariables(_attributes.get("rel"), 
                                                                           pm,
                                                                           PropertyMaps.GET_CURRENT)));
          f = FileOps.stringMakeRelativeTo(fil, rf);
        }
        String s = edu.rice.cs.util.StringOps.escapeFileName(f);
        sb.append(quot);
        sb.append(s);
        sb.append(quot);
      }
      catch(IOException e) {  }
      catch(SecurityException e) {  }
    }
    _value = sb.toString();
    if (_value.startsWith(_attributes.get("sep"))) {
      _value= _value.substring(_attributes.get("sep").length());
    }
  }
  
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put("sep", _sep);
    _attributes.put("rel", _dir);
    _attributes.put("squote", null);
    _attributes.put("dquote", null);
  }
}
