
package genj.edit.actions;

import genj.edit.Images;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

  
public class DelProperty extends AbstractChange {
  
  
  private Set candidates = new HashSet();
  
  
  public DelProperty(Property property) {
    super(property.getGedcom(), Images.imgDelEntity, resources.getString("delete"));
    candidates.add(property);
  }

  
  public DelProperty(Property[] properties) {
    super(properties[0].getGedcom(), Images.imgDelEntity, resources.getString("delete"));
    candidates.addAll(Arrays.asList(properties));
  }

  
  public void perform(Gedcom gedcom) throws GedcomException {
    for (Iterator candidate = candidates.iterator(); candidate.hasNext();) {
      Property prop  = (Property) candidate.next();
      prop.getParent().delProperty(prop);
    }
  }
  
} 

