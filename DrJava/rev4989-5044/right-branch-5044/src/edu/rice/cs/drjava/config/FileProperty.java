

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.FileOps;
import java.util.HashSet;
import java.io.*;


public class FileProperty extends DrJavaProperty {
  protected Thunk<File> _getFile;
  
  public FileProperty(String name, Thunk<File> getFile, String help) {
    super(name,help);
    _getFile = getFile;
    resetAttributes();
  }
  
  
  public String getCurrent(PropertyMaps pm) {
    update(pm);
    if (_value == null) { throw new IllegalArgumentException("DrJavaProperty value is null"); }
    _isCurrent = true;
    return _value;
  }

  
  public String toString() { return _value; }
  
  
  public boolean isCurrent() { return true; }
  
  
  public void invalidate() {
    
    invalidateOthers(new HashSet<DrJavaProperty>());
  }
  
  
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
    try {
      File f;
      if (_getFile == null || (f = _getFile.value()) == null) {
        _value = "";
        return;
      }
      if (_attributes.get("rel").equals("/")) {
        f = f.getAbsoluteFile();
        try { f = f.getCanonicalFile(); }
        catch(IOException ioe) { }
        _value = edu.rice.cs.util.StringOps.escapeFileName(f.toString());
      }
      else {
        File rf = new File(StringOps.unescapeFileName(StringOps.replaceVariables(_attributes.get("rel"), 
                                                                                        pm,
                                                                                        PropertyMaps.GET_CURRENT)));
        String s = FileOps.stringMakeRelativeTo(f,rf);
        _value = quot+edu.rice.cs.util.StringOps.escapeFileName(s)+quot;
      }
    }
    catch(IOException e) { _value = "(Error...)"; }
    catch(SecurityException e) { _value = "(Error...)"; }
  }    

  public void resetAttributes() {
    _attributes.clear();
    _attributes.put("rel", "/");
    _attributes.put("squote", null);
    _attributes.put("dquote", null);
  }
} 
