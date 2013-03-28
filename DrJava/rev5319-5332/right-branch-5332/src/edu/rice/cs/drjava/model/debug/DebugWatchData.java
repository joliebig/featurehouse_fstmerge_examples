

package edu.rice.cs.drjava.model.debug;


public class DebugWatchData {
  
  public static final String NO_VALUE = "<not found>";

  
  public static final String NO_TYPE = "";

  
  public static final String NOT_LOADED = "<not loaded>";

  private String _name;
  private String _value;
  private String _type;
  private boolean _showValue;
  private boolean _showType;
  private boolean _changed;

  
  public DebugWatchData(String name) {
    _name = name;
    _value = "";
    _type = "";
    _showValue = false;
    _showType = false;
    _changed = false;
  }

  
  public String getName() { return _name; }

  
  public String getValue() { return (_showValue) ? _value : ""; }

  
  public String getType() {
    return (_showType) ? _type : "";
  }

  
  public void setName(String name) {
    _name = name;
  }

  
  public void setValue(Object value) {
    _showValue = true;
    String valString = String.valueOf(value);
    if (!valString.equals(_value)) _changed = true;
    else _changed = false;
    _value = valString;
  }

  
  public void hideValueAndType() {
    _showValue = false;
    _showType = false;
    _changed = false;
  }

  
  public void setNoValue() {
    _showValue = true;
    _value = NO_VALUE;
    _changed = false;
  }

  
  public void setType(String type) {
    _showType = true;
    _type = type;
  }

  
  public void setNoType() {
    _showType = true;
    _type = NO_TYPE;
  }

  
  public void setTypeNotLoaded() {
    _showType = true;
    _type = NOT_LOADED;
  }

  
  public boolean isChanged() { return _changed; }

  
  public String toString() { return _type + " " + _name + ": " + _value; }
}
