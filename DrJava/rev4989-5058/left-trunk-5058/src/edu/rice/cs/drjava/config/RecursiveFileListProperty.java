

package edu.rice.cs.drjava.config;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.plt.text.TextUtil;


public class RecursiveFileListProperty extends FileListProperty {
  
  protected String _start;
  
  public RecursiveFileListProperty(String name, String sep, String dir, String start, String help) {
    super(name, sep, dir, help);
    _start = start;
    resetAttributes();
  }
  
  public static class RegexFilter implements FileFilter {
    protected String _regex;
    public RegexFilter(String regex) {
      _regex = regex;
    }
    public boolean accept(File pathname) {
      return pathname.getName().matches(_regex);
    }
  }
  public static class FileMaskFilter extends RegexFilter {
    private HashSet<File> _include = new HashSet<File>();
    private HashSet<File> _exclude = new HashSet<File>();
    public FileMaskFilter(String mask) {
      super(TextUtil.regexEscape(mask)
              .replaceAll("\\\\\\*",".*") 
              .replaceAll("\\\\\\?",".")); 
    }
    public boolean accept(File pathname) {
      if (_include.contains(pathname)) { return true; }
      if (_exclude.contains(pathname)) { return false; }
      return super.accept(pathname);
    }
    public void addIncludedFile(File f) { _include.add(f); }
    public void removeIncludedFile(File f) { _include.remove(f); }
    public void clearIncludedFile() { _include.clear(); }
    public void addExcludedFile(File f) { _exclude.add(f); }
    public void removeExcludedFile(File f) { _exclude.remove(f); }
    public void clearExcludedFile() { _exclude.clear(); }
  }
  
  
  protected List<File> getList(PropertyMaps pm) {
    FileMaskFilter fFilter = new FileMaskFilter(_attributes.get("filter"));
    FileMaskFilter fDirFilter = new FileMaskFilter(_attributes.get("dirfilter"));
    String start = StringOps.replaceVariables(_attributes.get("dir"), pm, PropertyMaps.GET_CURRENT);
    start = StringOps.unescapeFileName(start);
    File fStart = new File(start);
    
    if (fStart.isDirectory()) { fDirFilter.addIncludedFile(fStart); }
    Iterable<File> it = edu.rice.cs.plt.io.IOUtil.listFilesRecursively(fStart, fFilter, fDirFilter);

    ArrayList<File> l = new ArrayList<File>();
    for(File f: it) { l.add(f); }
    return l;
  }
  
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put("sep", _sep);
    _attributes.put("rel", _dir);
    _attributes.put("dir", _start);
    _attributes.put("filter", "*");
    _attributes.put("dirfilter", "*");
    _attributes.put("squote", null);
    _attributes.put("dquote", null);
  }
} 
