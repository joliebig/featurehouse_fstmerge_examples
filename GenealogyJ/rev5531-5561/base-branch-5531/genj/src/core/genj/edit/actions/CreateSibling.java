
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Options;
import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.view.ViewManager;


public class CreateSibling extends CreateRelationship {
  
  private Indi sibling;
  private boolean isBrotherNotSister;
  
  
  public CreateSibling(Indi sibling, ViewManager mgr, boolean isBrotherNotSister) {
    super(calcName(isBrotherNotSister), sibling.getGedcom(), Gedcom.INDI, mgr);
    this.sibling = sibling;
    this.isBrotherNotSister = isBrotherNotSister;
  }
  
  private static String calcName(boolean isBrotherNotSister) {
    
    String sibling = resources.getString("create.sibling", false);
    if (sibling==null) 
      return resources.getString( isBrotherNotSister ? "create.brother" : "create.sister" );
    
    return sibling + " (" + (isBrotherNotSister ? PropertySex.TXT_MALE : PropertySex.TXT_FEMALE) + ")";
  }
  
  
  public String getDescription() {
    
    return resources.getString("create.sibling.of", sibling);
  }

  
  protected Property change(Entity target, boolean targetIsNew) throws GedcomException {
    
    
    PropertyXRef CHIL;
    
    Fam[] fams = sibling.getFamiliesWhereChild();
    if (fams.length>0) {
      CHIL = fams[0].addChild((Indi)target);
    } else {
      
      
      fams = ((Indi)target).getFamiliesWhereChild();
      if (fams.length>0) {
        CHIL = fams[0].addChild(sibling);
      } else {

        
        Gedcom ged = sibling.getGedcom();
        Fam fam = (Fam)ged.createEntity(Gedcom.FAM);
        try {
          CHIL = fam.addChild((Indi)target);
        } catch (GedcomException e) {
          ged.deleteEntity(fam);
          throw e;
        }
        
        
        Indi husband = (Indi)ged.createEntity(Gedcom.INDI).addDefaultProperties();
        Indi wife = (Indi)ged.createEntity(Gedcom.INDI).addDefaultProperties();
        
        husband.setName("", sibling.getLastName());
        if (Options.getInstance().setWifeLastname)
          wife.setName("", sibling.getLastName());
        
        fam.setHusband(husband);
        fam.setWife(wife);
        fam.addChild(sibling);
      }

    }
    
    
    if (targetIsNew) {
      Indi indi = (Indi)target;
      indi.setName("", sibling.getLastName());        
      indi.setSex(isBrotherNotSister ? PropertySex.MALE : PropertySex.FEMALE);
    }    
    
    
    return CHIL.getTarget();
  }

}
