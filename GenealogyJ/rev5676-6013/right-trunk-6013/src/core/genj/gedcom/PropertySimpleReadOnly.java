
package genj.gedcom;


public class PropertySimpleReadOnly extends PropertySimpleValue {

  
  public PropertySimpleReadOnly() {
  }
  
  
  public PropertySimpleReadOnly(String tag) {
    super(tag);
  }
  
  
  public PropertySimpleReadOnly(String tag, String value) {
    super(tag, value);
  }

  
  public boolean isReadOnly() {
    return true;
  }

} 
