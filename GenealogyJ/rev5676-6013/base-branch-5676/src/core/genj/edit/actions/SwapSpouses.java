
package genj.edit.actions;

import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.view.ViewManager;


public class SwapSpouses extends AbstractChange {
  
  
  private Fam fam;
  
  
  public SwapSpouses(Fam family, ViewManager mgr) {
    super(family.getGedcom(), family.getImage(false), resources.getString("swap.spouses"), mgr);
    fam = family;
  }
  
  
  public void perform(Gedcom gedcom) throws GedcomException {
    fam.swapSpouses();
  }

} 
