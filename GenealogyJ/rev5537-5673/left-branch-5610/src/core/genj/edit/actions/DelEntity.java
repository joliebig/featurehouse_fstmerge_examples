
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.edit.Images;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;

  
public class DelEntity extends AbstractChange {
  
  
  private Entity candidate;
  
  
  public DelEntity(Entity entity) {
    super(entity.getGedcom(), Images.imgDelEntity, resources.getString("delete"));
    candidate = entity;
  }
  
  
  protected String getConfirmMessage() {
    
    return resources.getString("confirm.del", new String[] { 
      candidate.toString(), Gedcom.getName(candidate.getTag(),false), gedcom.getName() 
    });
  }

  
  protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
    candidate.getGedcom().deleteEntity(candidate);
    return null;
  }
  
} 

