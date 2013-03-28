
package genj.gedcom;

import genj.crypto.Enigma;
import genj.util.ReferenceSet;

import java.util.ArrayList;
import java.util.List;


public class PropertyChoiceValue extends PropertySimpleValue {

  
  public PropertyChoiceValue(String tag) {
    super(tag);
  }
  
  
  protected boolean remember(String oldValue, String newValue) {
    
    Gedcom gedcom = getGedcom();
    if (isTransient||gedcom==null)
      return false;
    ReferenceSet<String, Property> refSet = gedcom.getReferenceSet(getTag());
    
    newValue = newValue.intern();
    
    if (Enigma.isEncrypted(oldValue)) oldValue = "";
    if (Enigma.isEncrypted(newValue)) newValue = ""; 
    
    if (oldValue.length()>0) refSet.remove(oldValue, this);
    
    if (newValue.length()>0) refSet.add(newValue, this);
    
    return true;
  }
  
  
  public String[] getChoices(boolean sort) {
    
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return new String[0];
    return getChoices(gedcom, getTag(), sort);
  }
  
  
  public static String[] getChoices(final Gedcom gedcom, final String tag, boolean sort) {
    
    
    List<String> choices = gedcom.getReferenceSet(tag).getKeys(sort ? gedcom.getCollator() : null);

    
    return (String[])choices.toArray(new String[choices.size()]);
    
  }
  
  
  public static Property[] getSameChoices(Gedcom gedcom, String tag, boolean sort) {
    
    
    ReferenceSet<String, Property> references = gedcom.getReferenceSet(tag);
    List<String> choices = references.getKeys(sort ? gedcom.getCollator() : null);

    
    List<Property> result = new ArrayList<Property>(choices.size());
    for (String choice : choices) 
      result.addAll(references.getReferences(choice));
    
    
    return Property.toArray(result);
    
  }
  
  
  public Property[] getSameChoices() {
    
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return new Property[0];
    ReferenceSet<String, Property> refSet = gedcom.getReferenceSet(getTag());
    
    return toArray(refSet.getReferences(super.getValue()));
  }
  
  
  public void setValue(String value) {
    
    
    
    setValueInternal(value.intern());
  }
  
  
  public void setValue(String value, boolean global) {
    
    
    if (global) {
      
      Property[] others = getSameChoices();
      for (int i=0;i<others.length;i++) {
        Property other = others[i];
        if (other instanceof PropertyChoiceValue&&other!=this) 
          ((PropertyChoiceValue)other).setValue(value);
      }
    }    
      
    
    setValue(value);
    
    
  }

  private void setValueInternal(String value) {
    
    remember(super.getValue(), value);
    
    super.setValue(value);
  }

  
   void afterAddNotify() {
    
    super.afterAddNotify();
    
    remember("", super.getValue());
    
  }

  
   void beforeDelNotify() {
    
    remember(super.getValue(), "");
    
    super.beforeDelNotify();
  }

} 
