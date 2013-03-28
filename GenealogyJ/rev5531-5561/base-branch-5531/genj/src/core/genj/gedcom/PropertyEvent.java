
package genj.gedcom;


public class PropertyEvent extends Property {
  
  
  private String tag;
  
  
  private boolean knownToHaveHappened;

  
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

  
  public String getTag() {
    return tag;
  }

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    
    tag = meta.getTag();
    
    if (value.toLowerCase().equals("y"))
      knownToHaveHappened = true;
    
    return super.init(meta,value);
  }

  
  public String getValue() {
    return knownToHaveHappened ? "Y" : "";
  }

  
  public void setValue(String value) {
    setKnownToHaveHappened(value.toLowerCase().equals("y"));
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
