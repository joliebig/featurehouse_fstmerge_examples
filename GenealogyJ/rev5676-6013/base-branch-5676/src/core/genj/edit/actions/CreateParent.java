
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Options;
import genj.gedcom.Property;
import genj.gedcom.PropertyChild;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.view.ViewManager;


public class CreateParent extends CreateRelationship {
  
  
  private Indi child;
  private Fam family;
  
  
  public CreateParent(Fam family, ViewManager mgr) {
    super(resources.getString("create.parent"), family.getGedcom(), Gedcom.INDI, mgr);
    if (family.getNoOfSpouses()>=2)
      throw new IllegalArgumentException("can't create additional parent in family with husband and wife");
    this.family = family;
    this.child = null;
  }
  
  
  public CreateParent(Indi child, ViewManager mgr) {
    super(resources.getString("create.parent"), child.getGedcom(), Gedcom.INDI, mgr);
    this.child = child;
    
    
    Fam[] fams = child.getFamiliesWhereChild();
    for (int f = 0; f < fams.length; f++) {
      if (fams[f].getNoOfSpouses()<2) {
        family = fams[f];
        break;
      }
    }
    
    
  }

  
  public String getDescription() {
    
    if (child!=null)
      return resources.getString("create.parent.of", child);
    
    return resources.getString("create.parent.in", family);
  }

  
  public String getWarning(Entity indi) {
    
    
    if (child!=null&&family==null) {
      Fam fam =child.getFamilyWhereBiologicalChild();
      if (fam!=null)
        return PropertyChild.getLabelChildAlreadyinFamily(child, fam);
    }
    
    return null;
    
  }

  
  protected Property change(Entity parent, boolean parentIsNew) throws GedcomException {
    
    String lastname;
    Gedcom ged = parent.getGedcom();
    PropertyXRef FAMS;
    
    
    if (family!=null) {

      FAMS = family.setSpouse((Indi)parent).getTarget();
      Indi other = family.getOtherSpouse((Indi)parent);
      lastname = other!=null ? other.getLastName() : "";
      
    } else { 

      
      lastname = child.getLastName();

      
      family = (Fam)ged.createEntity(Gedcom.FAM);
      family.addChild(child);
      family.addDefaultProperties();
      
      
      FAMS = family.setSpouse((Indi)parent).getTarget();
      
      
      
      if (family.getNoOfSpouses()<2) {
        Indi spouse = (Indi)ged.createEntity(Gedcom.INDI);
        spouse.addDefaultProperties();
        family.setSpouse(spouse);
        if ( Options.getInstance().setWifeLastname || spouse.getSex() == PropertySex.MALE)  
        spouse.setName("", lastname);
      }
      
    }
    
    
    if (parentIsNew && (((Indi)parent).getSex() == PropertySex.MALE||Options.getInstance().setWifeLastname)) 
      ((Indi)parent).setName("", lastname);

    
    return FAMS;      
  }

}
