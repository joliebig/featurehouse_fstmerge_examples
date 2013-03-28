
package genj.gedcom;



public class PropertySimpleValue extends Property {

  
  private String value;

  
  public PropertySimpleValue(String tag) {
    super(tag);
  }

  
  public PropertySimpleValue(String tag, String value) {
    super(tag);
    this.value = value;
  }

  
  public String getValue() {
    if (value==null) return "";
    return value;
  }

  
  public void setValue(String value) {
    String old = getValue();
    this.value=value;
    propagatePropertyChanged(this, old);
  }
  
} 
