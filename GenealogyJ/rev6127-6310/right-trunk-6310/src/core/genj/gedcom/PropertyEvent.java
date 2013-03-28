
package genj.gedcom;

import genj.gedcom.time.Delta;
import genj.util.swing.ImageIcon;


public class PropertyEvent extends Property {
  
  public static ImageIcon IMG = Grammar.V55.getMeta(new TagPath("INDI:EVEN")).getImage();
  
  
  private boolean knownToHaveHappened;

  
  public PropertyEvent(String tag) {
    super(tag);
  }
  
  
  public PropertyDate getDate() {
    return getDate(true);
  }

  
  public PropertyDate getDate(boolean valid) {

    
    Property prop = getProperty("DATE",valid);
    if (prop==null) 
      return null;

    
    return (PropertyDate)prop;
  }

  
  public String getDateAsString() {
    Property date = getProperty("DATE");
    return date!=null ? date.getValue() : "";
  }

  
  public String getValue() {
    return knownToHaveHappened ? "Y" : "";
  }

  
  public void setValue(String value) {
    setKnownToHaveHappened(value.toLowerCase().equals("y"));
  }
  
  @Override
  void propagatePropertyChanged(Property property, String oldValue) {
    super.propagatePropertyChanged(property, oldValue);
    
    
    if (property instanceof PropertyDate && getProperty("DATE")==property && getParent() instanceof Indi) {
      
      if (getParent().getProperty("BIRT") == this) {
        for (PropertyEvent event : getParent().getProperties(PropertyEvent.class)) {
          if (event!=this)
            event.updateAge((PropertyDate)property);
        }
      } else if (!"BIRT".equals(getTag())){
        updateAge();
      }
    }

    
  }


  
  public void updateAge() {
    
    if (!(getParent() instanceof Indi ))
      return;
    
    updateAge( ((Indi)getParent()).getBirthDate() );
    
  }
  
   void updateAge(PropertyDate birt) {
    
    
    PropertyDate date = getDate(true);
    if (date==birt)
      return;
    
    
    PropertyAge age = (PropertyAge) getProperty("AGE");
    if (age==null) {
      if (date==null || !Options.getInstance().isAddAge)
        return;
      age = (PropertyAge)addProperty("AGE", "");
    }
    
    
    if (date==null||birt==null||!birt.isValid()) {
      
      return;
    }
    
    
    if (birt.getStart().compareTo(date.getStart())>=0)
      age.setValue("");
    else
      age.setValue(Delta.get(birt.getStart(), date.getStart()));
    
    
  }

  
  public static TagPath[] getTagPaths(Gedcom gedcom) {
    return gedcom.getGrammar().getAllPaths(null, PropertyEvent.class);  
  }
  
  
  public Boolean isKnownToHaveHappened() {
    
    if (getTag().equals("EVEN"))
      return null;
    return new Boolean(knownToHaveHappened);
  }

  
  public void setKnownToHaveHappened(boolean set) {
    String old = getValue();
    knownToHaveHappened = set;
    propagatePropertyChanged(this, old);
  }


















} 
