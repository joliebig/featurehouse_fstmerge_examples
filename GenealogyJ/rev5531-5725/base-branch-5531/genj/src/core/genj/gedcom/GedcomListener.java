
package genj.gedcom;


public interface GedcomListener {
  
  public void gedcomEntityAdded(Gedcom gedcom, Entity entity);

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity);
  
  public void gedcomPropertyChanged(Gedcom gedcom, Property property);
  
  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added);
  
  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted);
  
} 
