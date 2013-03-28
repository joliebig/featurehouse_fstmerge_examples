
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;


public class CreateAssociation extends CreateRelationship {
  
  private Property target;
  
  
  public CreateAssociation(Property target) {
    super(resources.getString("create.association"), target.getGedcom(), Gedcom.INDI);
    this.target = target;
    
  }

  
  public String getDescription() {
    return resources.getString("create.association.with", Gedcom.getName(target.getTag()), target.getEntity().toString() );
  }

  
  protected Property change(Entity source, boolean targetIsNew) throws GedcomException {
    
    Indi indi = (Indi)source;

    
    PropertyXRef asso = (PropertyXRef)indi.addProperty("ASSO", '@'+target.getEntity().getId()+'@');
    
    
    TagPath anchor = target.getPath(true);
    Property rela = asso.addProperty("RELA", anchor==null ? "" : '@'+anchor.toString() );

    
    try {
      asso.link();
    } catch (GedcomException e) {
      indi.delProperty(asso);
      throw e;
    }
    
    
    return rela;
  }

}
