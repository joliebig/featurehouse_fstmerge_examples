

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.plt.lambda.Lambda2;
import java.util.*;


public class PropertyMaps implements Cloneable {
  
  protected Map<String,Map<String,DrJavaProperty>> _props = new TreeMap<String,Map<String,DrJavaProperty>>();
  
  
  public static final PropertyMaps TEMPLATE = new PropertyMaps();
  
  static {
    for(Map.Entry<Object,Object> es: System.getProperties().entrySet()) {
      TEMPLATE.setProperty("Java", new JavaSystemProperty(es.getKey().toString()));
    }
    
    OptionMap om = DrJava.getConfig().getOptionMap();
    for (OptionParser<?> op : om.keys()) {
      String key = "config."+op.getName();
      TEMPLATE.setProperty("Config", new ConfigProperty(key));
    }
  }
  
  
  public PropertyMaps() { }

  
  public DrJavaProperty getProperty(String category, String name) {
    Map<String,DrJavaProperty> m = _props.get(category);
    if (m==null) { throw new IllegalArgumentException("DrJavaProperty category unknown."); }
    return m.get(name);
  }
  
  
  public DrJavaProperty getProperty(String key) {
    for(String category: _props.keySet()) {
      DrJavaProperty p = getProperty(category, key);
      if (p!=null) { return p; }
    }
    return null;
  }
  
  
  public void removeProperty(DrJavaProperty p) {
    for(String category: _props.keySet()) {
      _props.get(category).remove(p);
    }
  }
  
  
  public DrJavaProperty setProperty(String category, DrJavaProperty p) {
    Map<String,DrJavaProperty> m = _props.get(category);
    if (m==null) { m = new HashMap<String,DrJavaProperty>(); _props.put(category,m); }
    m.put(p.getName(), p);
    return p;
  }
  
  
  public void clearCategory(String category) {
    _props.remove(category);
  }
  
  
  public Set<String> getCategories() { return _props.keySet(); }

  
  public Map<String, DrJavaProperty> getProperties(String category) {
    Map<String,DrJavaProperty> m = _props.get(category);
    if (m==null) { throw new IllegalArgumentException("DrJavaProperty category unknown."); }
    return m;
  }
  
  
  public static final Lambda2<DrJavaProperty,PropertyMaps,String> GET_LAZY = new Lambda2<DrJavaProperty,PropertyMaps,String>() {
    public String value(DrJavaProperty p, PropertyMaps pm) { return p.getLazy(pm);  }
  };
  
  
  public static final Lambda2<DrJavaProperty,PropertyMaps,String> GET_CURRENT = new Lambda2<DrJavaProperty,PropertyMaps,String>() {
    public String value(DrJavaProperty p, PropertyMaps pm) { return p.getCurrent(pm); }
  };
  
  protected HashMap<String,Stack<VariableProperty>> _variables = new HashMap<String,Stack<VariableProperty>>();
  
  
  protected static final String VARIABLES_CATEGORY = "$Variables$";
  
  
  public void clearVariables() {
    _props.remove(VARIABLES_CATEGORY);
  }
  
  
  public void addVariable(String name, String value) {
    for(String category: _props.keySet()) {
      if (category.equals(VARIABLES_CATEGORY)) continue;
      if (getProperty(category, name)!=null) {
        throw new IllegalArgumentException("Variable "+name+" already used for a built-in property");
      }
    }
    
    VariableProperty p = new VariableProperty(name, value);
    setProperty(VARIABLES_CATEGORY, p);
    Stack<VariableProperty> varStack = _variables.get(name);
    if (varStack==null) { varStack = new Stack<VariableProperty>(); _variables.put(name,varStack); }
    varStack.push(p);
  }
  
  
  public void setVariable(String name, String value) {
    Stack<VariableProperty> varStack = _variables.get(name);
    if ((varStack==null) ||
        (varStack.empty())) { throw new IllegalArgumentException("Variable "+name+" does not exist."); }
    VariableProperty p = varStack.peek();
    p.setValue(value);
  }
  
  
  public void removeVariable(String name) {
    Stack<VariableProperty> varStack = _variables.get(name);
    if ((varStack==null) ||
        (varStack.empty())) { throw new IllegalArgumentException("Variable "+name+" does not exist."); }
    VariableProperty p = varStack.pop();
    if (varStack.empty()) {
      
      
      _variables.remove(name);
      
      removeProperty(p);
    }
    else {
      
      
      setProperty(VARIABLES_CATEGORY, varStack.peek());
    }
  }
  
  
  public PropertyMaps clone() throws CloneNotSupportedException {
    PropertyMaps clone = new PropertyMaps();
    clone._props.clear();
    for(String category: _props.keySet()) {
      for (String key: _props.get(category).keySet()) {
        clone.setProperty(category, getProperty(key));
      }
    }
    clone._variables.clear();
    for(String name: _variables.keySet()) {
      Stack<VariableProperty> stack = new Stack<VariableProperty>();
      for (VariableProperty v: _variables.get(name)) {
        stack.add(new VariableProperty(v.getName(),v.getCurrent(this)));
      }
      clone._variables.put(name, stack);
    }
    return clone;
  }
} 
