
package genj.gedcom;



public class PropertyNumericValue extends Property {
  
  
  private Class box = Integer.class;

  
  private Comparable value = "";
  
  
  private String tag;

  
  public PropertyNumericValue() {
  }

  
  public String getTag() {
    return tag;
  }
  
  
   Property init(MetaProperty meta, String value) {
    tag = meta.getTag();
    try {
      return super.init(meta, value);
    } catch (GedcomException e) {
      
    }
    return this;
  }

  
  public String getValue() {
    return value.toString();
  }

  
  public void setValue(String set) {
    
    
    String old = getValue();
    
    
    try {
      value = (Comparable)box.getConstructor(new Class[]{String.class}).newInstance(new Object[]{set});
    } catch (Throwable t) {
      value = set;
    }
    
    
    propagatePropertyChanged(this, old);
  }
  
  
  public int compareTo(Property other) {
    PropertyNumericValue that = (PropertyNumericValue)other;
    
    if (that.value.getClass()!=this.value.getClass())
      return super.compareTo(other);
    
    return this.value.compareTo(that.value);
  }
  
} 
