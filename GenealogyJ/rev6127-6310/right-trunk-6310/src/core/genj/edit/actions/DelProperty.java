
package genj.edit.actions;

import genj.edit.Images;
import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

  
public class DelProperty extends AbstractChange {
  
  
  private Set<Property> candidates = new HashSet<Property>();
  
  
  public DelProperty(Property property) {
    super(property.getGedcom(), Images.imgDel, resources.getString("delete"));
    candidates.add(property);
  }

  
  public DelProperty(List<? extends Property> properties) {
    super(properties.get(0).getGedcom(), Images.imgDel, resources.getString("delete"));
    candidates.addAll(properties);
  }
  
  @Override
  protected String getConfirmMessage() {
    StringBuffer txt = new StringBuffer();
    txt.append(resources.getString("confirm.del.props", candidates.size()));
    txt.append("\n");
    int i=0; for (Property prop : candidates)  {
      if (i++>16) {
        txt.append("...");
        break;
      }
      txt.append(prop.toString());
      txt.append("\n");
    }
    return txt.toString();
  }
  
  
  protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
    
    for (Property prop : candidates) 
      prop.getParent().delProperty(prop);
    
    return null;
  }
  
} 

