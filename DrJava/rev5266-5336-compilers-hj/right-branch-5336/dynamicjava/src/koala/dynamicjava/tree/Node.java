



package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public abstract class Node implements SourceInfo.Wrapper {
  private final Map<String,Object> properties;
  private SourceInfo sourceInfo;
  
  
  protected Node(SourceInfo si) {
    assert si != null;
    sourceInfo = si;
    properties = new HashMap<String, Object>();
  } 
  
  
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }
  
  
  public void setSourceInfo(SourceInfo si) {
    assert si != null;
    sourceInfo = si;
  }
  
  
  
  
  
  
  public void setProperty(String name, Object value) {
    properties.put(name, value);
  }
  
  
  public Object getProperty(String name) {
    if (!properties.containsKey(name)) { 
      throw new IllegalStateException("Property '" + name + "' is not initialized");
    }
    return properties.get(name);
  }
  
  
  public Set<String> getProperties() {
    return properties.keySet();
  }
  
  
  public boolean hasProperty(String name) {
    return properties.containsKey(name);
  }
  
  
  public void archiveProperties(String prefix) {
    Map<String, Object> newProps = new HashMap<String, Object>();
    for (Map.Entry<String, Object> e : properties.entrySet()) { newProps.put(prefix + e.getKey(), e.getValue()); }
    properties.clear();
    properties.putAll(newProps);
  }
  
  
  public abstract <T> T acceptVisitor(Visitor<T> visitor);
  
}
