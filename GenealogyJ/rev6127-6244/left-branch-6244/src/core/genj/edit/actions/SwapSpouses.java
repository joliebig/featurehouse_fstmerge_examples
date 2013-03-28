
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.gedcom.Context;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;


public class SwapSpouses extends AbstractChange {
  
  
  private Fam fam;
  
  
  public SwapSpouses(Fam family) {
    super(family.getGedcom(), family.getImage(false), resources.getString("swap.spouses"));
    fam = family;
  }
  
  
  protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
    fam.swapSpouses();
    return null;
  }

} 
