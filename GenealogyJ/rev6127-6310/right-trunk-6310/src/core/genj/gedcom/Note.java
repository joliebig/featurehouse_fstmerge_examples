
package genj.gedcom;

import java.util.List;
import java.util.regex.Pattern;





public class Note extends Entity implements MultiLineProperty {

  
  private PropertyMultilineValue delegate;
  
  
  public Note(String tag, String id) {
    super(tag, id);
    assertTag(Gedcom.NOTE);
  }
  
  
   void addNotify(Gedcom ged) {
    
    
    super.addNotify(ged);

    
    
    if (delegate==null) {
      delegate = (PropertyMultilineValue)addProperty("NOTE", "");
      delegate.isTransient = true;
    }
    
    
  }

  
  public PropertyMultilineValue getDelegate() {
    return delegate;
  }
  
  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return delegate.getDisplayValue();
  }

  
  public void setValue(String newValue) {
    
    delegate.setValue(newValue);
  }
  
  
  public void delProperty(Property which) {
    
    if (which!=delegate) 
      super.delProperty(which);
  }

    
  
  public String getValue() {
    return delegate.getValue();
  }
  
  public List<Property> findProperties(Pattern tag, Pattern value) {
    
    List<Property> result = super.findProperties(tag, value);
    
    result.remove(this);
    
    return result;
  }
  
  
  public Iterator getLineIterator() {
    return delegate.getLineIterator();
  }

  
  public Collector getLineCollector() {
    return delegate.getLineCollector();
  }
  
  
  public boolean isPrivate() {
    return delegate.isPrivate();
  }

  
  public void setPrivate(boolean set, boolean recursively) {
    delegate.setPrivate(set, recursively);
  }

} 
