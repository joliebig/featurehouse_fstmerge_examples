
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyChild;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.view.ViewManager;


public class CreateChild extends CreateRelationship {
  
  
  private Entity parentOrFamily;
  
  private boolean male;
  
  
  public CreateChild(Fam family, ViewManager mgr, boolean male) {
    super(calcText(male), family.getGedcom(), Gedcom.INDI, mgr);
    this.male = male;
    setImage(male ? PropertyChild.IMG_MALE : PropertyChild.IMG_FEMALE);
    this.parentOrFamily = family;
  }
  
  
  public CreateChild(Indi parent, ViewManager mgr, boolean male) {
    super(calcText(male), parent.getGedcom(), Gedcom.INDI, mgr);
    this.male = male;
    setImage(male ? PropertyChild.IMG_MALE : PropertyChild.IMG_FEMALE);
    this.parentOrFamily = parent;
  }
  
  private static String calcText(boolean male) {
    String txt = resources.getString("create.child" , false);
    if (txt!=null)
      return txt  + " ("+(male?PropertySex.TXT_MALE:PropertySex.TXT_FEMALE)+")";
    return resources.getString("create." + (male?"son":"daughter"));
  }

  
  public String getDescription() {
    
    if (parentOrFamily instanceof Indi)
      return resources.getString("create.child.of", parentOrFamily);
    
    return resources.getString("create.child.in", parentOrFamily);
  }

  
  public String getWarning(Entity indi) {
    
    
    if (indi!=null) { 
      
      Fam fam = ((Indi)indi).getFamilyWhereBiologicalChild();
      if (fam!=null)
        return PropertyChild.getLabelChildAlreadyinFamily((Indi)indi, fam);
    }
    
    
    return null;
  }

  
  protected Property change(Entity target, boolean targetIsNew) throws GedcomException {
    
    
    Indi child = (Indi)target;
    Gedcom ged = child.getGedcom();
    PropertyXRef CHIL;
    
    
    Fam family;
    if (parentOrFamily instanceof Indi) {

      Indi parent = (Indi)parentOrFamily;
      
      
      Fam[] fams = parent.getFamiliesWhereSpouse();
      if (fams.length>0) {
        
        family = fams[0];
        CHIL = family.addChild(child);
      } else {
        
        family = (Fam)ged.createEntity(Gedcom.FAM);
        try {
          CHIL = family.addChild(child);
        } catch (GedcomException e) {
          ged.deleteEntity(family);
          throw e;
        }
        
        family.setSpouse(parent);
        
        family.setSpouse((Indi)ged.createEntity(Gedcom.INDI).addDefaultProperties());
      }
      
    } else {
      
      
      family = (Fam)parentOrFamily;
      CHIL = family.addChild(child);
    
    }
    
    
    if (targetIsNew) {
      Indi parent  = family.getHusband();
      if (parent==null) parent = family.getWife();
      if (parent!=null)
        child.setName("", parent.getLastName());
      child.setSex(male ? PropertySex.MALE : PropertySex.FEMALE);
    }
    
    
    return CHIL.getTarget();
  }

}
