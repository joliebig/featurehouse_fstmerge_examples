
package genj.edit.actions;

import genj.edit.Images;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.view.ViewManager;

  
public class DelEntity extends AbstractChange {
  
  
  private Entity candidate;
  
  
  public DelEntity(Entity entity, ViewManager manager) {
    super(entity.getGedcom(), Images.imgDelEntity, resources.getString("delete"), manager);
    candidate = entity;
  }
  
  
  protected String getConfirmMessage() {
    
    return resources.getString("confirm.del", new String[] { 
      candidate.toString(), Gedcom.getName(candidate.getTag(),false), gedcom.getName() 
    });
  }

  
  public void perform(Gedcom gedcom) throws GedcomException {
    candidate.getGedcom().deleteEntity(candidate);
  }
  
} 

