
package genj.edit.actions;

import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;


public class SwapSpouses extends AbstractChange {
  
  
  private Fam fam;
  
  
  public SwapSpouses(Fam family) {
    super(family.getGedcom(), family.getImage(false), resources.getString("swap.spouses"));
    fam = family;
  }
  
  
  public void perform(Gedcom gedcom) throws GedcomException {
    fam.swapSpouses();
  }

} 
