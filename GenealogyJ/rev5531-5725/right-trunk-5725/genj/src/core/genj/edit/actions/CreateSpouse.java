
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.view.ViewManager;


public class CreateSpouse extends CreateRelationship {
  
  private Indi spouse;
  
  
  public CreateSpouse(Indi spouse, ViewManager mgr) {
    super( resources.getString("create.spouse"), spouse.getGedcom(), Gedcom.INDI, mgr);
    this.spouse = spouse;
  }
  
  
  public String getWarning(Entity target) {
    int n = spouse.getNoOfFams();
    if (n>0)
      return resources.getString("create.spouse.warning", new String[]{ spouse.toString(), ""+n });
    return null;
  }
  
  
  public String getDescription() {
    
    return resources.getString("create.spouse.of", spouse);
  }

  
  protected Property change(Entity target, boolean targetIsNew) throws GedcomException {
    
    
    Fam[] fams = spouse.getFamiliesWhereSpouse();
    Fam fam = null;
    if (fams.length>0)
      fam = fams[0];
    if (fam==null||fam.getNoOfSpouses()>=2) {
      fam = (Fam)spouse.getGedcom().createEntity(Gedcom.FAM).addDefaultProperties();
      fam.setSpouse(spouse);
    }

    
    return fam.setSpouse((Indi)target).getTarget();
  }
  

}
