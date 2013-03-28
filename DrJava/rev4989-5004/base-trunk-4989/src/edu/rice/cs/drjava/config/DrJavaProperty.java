

package edu.rice.cs.drjava.config;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import edu.rice.cs.plt.lambda.Lambda;


public abstract class DrJavaProperty implements Cloneable {
  
  public volatile boolean DEACTIVATED_DUE_TO_ERROR = false;
  
  
  protected String _name;
  
  protected String _value = "--uninitialized--";
  
  protected boolean _isCurrent = false;
  
  protected String _help = "Help unavailable.";
  
  protected HashMap<String,String> _attributes = new HashMap<String,String>();
  
  protected Set<DrJavaProperty> _listening = new HashSet<DrJavaProperty>();
  
  
  public DrJavaProperty(String name, String help) {
    if (name == null) { throw new IllegalArgumentException("DrJavaProperty name is null"); }
    _name = name;
    if (help != null) { _help = help; } 
    resetAttributes();
  }

  
  public DrJavaProperty(String name, String value, String help) {
    this(name, help);
    if (value==null) { throw new IllegalArgumentException("DrJavaProperty value is null"); }
    if (help!=null) { _help = help; } 
    _value = value;
    _isCurrent = true;
  }
  
  
  public String getName() { return _name; }
  
  
  public String getCurrent(PropertyMaps pm) {
    if (!isCurrent()) {
      update(pm);
      if (_value == null) { throw new IllegalArgumentException("DrJavaProperty value is null"); }
      _isCurrent = true;
    }
    return _value;
  }

  
  public String getLazy(PropertyMaps pm) {
    if (_value == null) { throw new IllegalArgumentException("DrJavaProperty value is null"); }
    return _value;
  }
  
  
  public abstract void update(PropertyMaps pm);
  
  
  public void resetAttributes() { _attributes.clear(); }
  
  
  public void setAttribute(String key, String value) {
    if (!_attributes.containsKey(key)) {
      throw new IllegalArgumentException("Attribute "+key+" not known to property "+_name);
    }
    _attributes.put(key, value);
  }
  
  
  public void setAttributes(HashMap<String,String> attrs, Lambda<String,String> replaceLambda) {
    for(Map.Entry<String,String> e: attrs.entrySet()) {
      setAttribute(e.getKey(), replaceLambda.value(e.getValue()));
    }
  }
  
  
  public String getAttribute(String key) {
    if (!_attributes.containsKey(key)) {
      throw new IllegalArgumentException("Attribute "+key+" not known to property "+_name);
    }
    return _attributes.get(key);
  }
  
  
  public String toString() { return _value; }
  
  
  public String getHelp() { return _help; }

  
  public boolean isCurrent() { return _isCurrent; }
    
  
  public void invalidate() {
    _invalidate();
    invalidateOthers(new HashSet<DrJavaProperty>());
  }
  
  
  protected void _invalidate() { _isCurrent = false; }
  
  public DrJavaProperty listenToInvalidatesOf(DrJavaProperty other) {
    if (other == this) {
      DEACTIVATED_DUE_TO_ERROR = true;
      RuntimeException e = new IllegalArgumentException("Property cannot listen for invalidation of itself. "+
                                                        "Variables for external processes will not function correctly anymore. "+
                                                        "This is a SERIOUS programming error. Please notify the DrJava team.");
      edu.rice.cs.drjava.ui.DrJavaErrorHandler.record(e);
      throw e;
    }
    other._listening.add(this);
    return this;
  }
  
  
  public boolean equals(Object other) {
    if (other == null || other.getClass() != this.getClass()) return false;

    DrJavaProperty o = (DrJavaProperty) other;
    return _name.equals(o._name);

  }

  
  public int hashCode() { return _name.hashCode(); }
  
  
  protected void invalidateOthers(Set<DrJavaProperty> alreadyVisited) {
    if (DEACTIVATED_DUE_TO_ERROR) { return; }          
    if (alreadyVisited.contains(this)) {
      Iterator<DrJavaProperty> it = alreadyVisited.iterator();
      StringBuilder sb = new StringBuilder("Invalidating ");
      sb.append(getName());
      sb.append(" after already having invalidated ");
      boolean first = true;
      while (it.hasNext()) {
        if (first) { first = false; } 
        else { sb.append(", "); }
        sb.append(it.next().getName());
      }
      sb.append(". Variables for external processes will not function correctly anymore. "+
                "This is a SERIOUS programming error. Please notify the DrJava team.");
      DEACTIVATED_DUE_TO_ERROR = true;
      RuntimeException e = new InfiniteLoopException(sb.toString());
      edu.rice.cs.drjava.ui.DrJavaErrorHandler.record(e);
      throw e;
    }
    alreadyVisited.add(this);
    Iterator<DrJavaProperty> it = _listening.iterator();
    while(it.hasNext()) {
      DrJavaProperty prop = it.next();
      prop._invalidate();
      prop.invalidateOthers(alreadyVisited);
    }
  }
  
  
  public static class InfiniteLoopException extends RuntimeException {
    public InfiniteLoopException(String s) { super(s); } 
  }
} 
