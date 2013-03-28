
package genj.gedcom;


public class Entity extends Property {
  
  
  private Gedcom gedcom;
  
  
  private String id;
  
  
  private String tag;
  
  
  private String value;
  
  
   void addNotify(Gedcom ged) {
    
    gedcom = ged;
    
    ged.propagateEntityAdded(this);
    
  }
  
  
   void beforeDelNotify() {
    
    
    delProperties();

    
    gedcom.propagateEntityDeleted(this);
    
    
    gedcom = null;
    
    
  }
  
  
  public PropertyChange getLastChange() {
    return (PropertyChange)getProperty("CHAN");
  }

  
  public Gedcom getGedcom() {
    return gedcom;
  }
  
  
  public Entity getEntity() {
    return this;
  }
  
  public boolean isConnected() {
    for (PropertyXRef xref : getProperties(PropertyXRef.class)) {
      if (xref.isValid())
        return true;
    }
    return false;
  }

  
  public void setId(String set) throws GedcomException {
    
    
    String old = id;
    id = set;
    
    
    if (gedcom!=null) try {
      gedcom.propagateEntityIDChanged(this, old);
    } catch (Throwable t) {
      id = old;
    }

    
  }
  
  
  public String getId() {
    return id;
  }
  
  
   void init(String setTag, String setId) {
    tag = setTag;
    id = setId;
  }
  
  
  public final String toString() {
    return toString(true);
  }
  
  public final String toString(boolean showIds) {
    
    StringBuffer buf = new StringBuffer();
    buf.append(getToStringPrefix(showIds));
    if (buf.length()==0)
      buf.append(getTag());
    if (showIds) {
      buf.append(" (");
      buf.append(getId());
      buf.append(')');
    }
    return buf.toString();
  }

  protected String getToStringPrefix(boolean showIds) {
    return getTag();
  }
  
  
  public String getTag() {
    return tag;
  }
  
  
  public String getValue() {
    return value!=null?value : "";
  }
  
  
  public void setValue(String set) {
    value = set;
  }

  
  public int compareTo(Property other) {
    
    if (!(other instanceof Entity))
      throw new IllegalArgumentException("Cannot compare entity to property");
    
    return getID() - ((Entity)other).getID(); 
  }

  
  private int getID() throws NumberFormatException {
    
    int 
      start = 0,
      end   = id.length()-1;
      
    while (start<=end&&!Character.isDigit(id.charAt(start))) start++;
    while (end>=start&&!Character.isDigit(id.charAt(end))) end--;

    if (end<start) throw new NumberFormatException();
         
    return Integer.parseInt(id.substring(start, end+1));
  }

  
  public String format(String propertyTag, String format) {
    Property p = getProperty(propertyTag);
    return p!=null ? p.format(format) : "";
  }

  
  void propagateXRefLinked(PropertyXRef property1, PropertyXRef property2) {
    if (gedcom!=null)
      gedcom.propagateXRefLinked(property1, property2);
  }

  void propagateXRefUnlinked(PropertyXRef property1, PropertyXRef property2) {
    if (gedcom!=null)
      gedcom.propagateXRefUnlinked(property1, property2);
  }

  void propagatePropertyAdded(Property container, int pos, Property added) {
    if (gedcom!=null)
      gedcom.propagatePropertyAdded(this, container, pos, added);
  }

  void propagatePropertyDeleted(Property container, int pos, Property deleted) {
    if (gedcom!=null)
      gedcom.propagatePropertyDeleted(this, container, pos, deleted);
  }

  void propagatePropertyChanged(Property property, String oldValue) {
    if (gedcom!=null)
      gedcom.propagatePropertyChanged(this, property, oldValue);
  }

  void propagatePropertyMoved(Property property, Property moved, int from, int to) {
    if (gedcom!=null)
      gedcom.propagatePropertyMoved(property, moved, from, to);
  }
} 
