
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyAlias;
import genj.view.ViewManager;


public class CreateAlias extends CreateRelationship {
  
  private Indi source;
  
  
  public CreateAlias(Indi source, ViewManager mgr) {
    super(resources.getString("create.alias"), source.getGedcom(), Gedcom.INDI, mgr);
    this.source = source;
  }

  
  public String getDescription() {
    return resources.getString("create.alias.of", source.toString() );
  }

  
  protected Property change(Entity target, boolean targetIsNew) throws GedcomException {
    
    
    PropertyAlias alias = (PropertyAlias)source.addProperty("ALIA", '@'+target.getEntity().getId()+'@');
    
    
    try {
      alias.link();
    } catch (GedcomException e) {
      source.delProperty(alias);
      throw e;
    }
    
    
    return alias;
  }

}
