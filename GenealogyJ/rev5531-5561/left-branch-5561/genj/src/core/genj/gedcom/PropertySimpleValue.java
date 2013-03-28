
package genj.gedcom;



public class PropertySimpleValue extends Property {

  
  private String tag;

  
  private String value;

  
  public PropertySimpleValue() {
  }

  
  public PropertySimpleValue(String tag) {
    this.tag = tag;
  }

  
  public PropertySimpleValue(String tag, String value) {
    this.tag = tag;
    this.value = value;
  }

  
  public String getTag() {
    return tag;
  }
  
  
   Property init(MetaProperty meta, String value) {
    tag = meta.getTag();
    try {
      return super.init(meta, value);
    } catch (GedcomException e) {
      
      return this;
    }
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
