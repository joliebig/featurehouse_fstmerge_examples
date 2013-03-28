
package genj.gedcom;



public class PropertyNumericValue extends Property {
  
  private Class<?> box = Integer.class; 
  
  
  private Object value = "";
  
  
  public PropertyNumericValue(String tag) {
    super(tag);
  }

  
  public String getValue() {
    return value.toString();
  }

  
  @SuppressWarnings("unchecked")
  public void setValue(String set) {
    
    
    String old = getValue();
    
    
    try {
      value = (Comparable)box.getConstructor(new Class[]{String.class}).newInstance(new Object[]{set});
    } catch (Throwable t) {
      value = set;
    }
    
    
    propagatePropertyChanged(this, old);
  }
  
  
  @SuppressWarnings("unchecked")
  public int compareTo(Property other) {
    PropertyNumericValue that = (PropertyNumericValue)other;
    
    if (that.value.getClass()!=this.value.getClass())
      return super.compareTo(other);
    
    return ((Comparable<Object>)(this.value)).compareTo((Comparable<Object>)(that.value));
  }
  
} 
