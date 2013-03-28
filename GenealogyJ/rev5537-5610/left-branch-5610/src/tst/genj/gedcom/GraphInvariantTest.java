

package genj.gedcom;

import genj.io.GedcomReaderFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;


public class GraphInvariantTest extends TestCase {
  
  private Gedcom gedcom;

  
  protected void setUp() throws IOException {
    
    Logger.getLogger("").setLevel(Level.OFF);

    
    gedcom = GedcomReaderFactory.createReader(getClass().getResourceAsStream("graphinvariants.ged"), null).read();
    
  }
  
  
  private Indi indi(int id) {
    return (Indi)gedcom.getEntity(Gedcom.INDI, Integer.toString(id));
  }
  
  
  private Fam fam(int id) {
    return (Fam)gedcom.getEntity(Gedcom.FAM, Integer.toString(id));
  }

  
  private void link(Entity entity, String tag, int id) throws GedcomException {
    PropertyXRef xref = (PropertyXRef)entity.addProperty(tag, "@"+id+"@");
    xref.link();
  }

  
  public void testCircleThroughFAMC() {

    
    int[] ancestorsToCheck = { 0, 2, 3, 6, 7, 8, 9};
    int famToRevisit = 0;
    for (int i=0; i<ancestorsToCheck.length; i++)
      try {
        link(indi(ancestorsToCheck[i]), "FAMC", famToRevisit);
        fail("didn't recognize circle through FAMC between indi "+ancestorsToCheck[i]+" and family "+famToRevisit);
      } catch (GedcomException e) {
        
      }
    
  }

  
  public void testCircleThroughCHIL() {

    
    int[] famsToCheck = { 0, 1, 3 };
    for (int f=0; f<famsToCheck.length; f++)
      try {
        link(fam(famsToCheck[f]), "CHIL", 6);
        fail("didn't recognize circle through CHIL between family "+famsToCheck[f]+" and indi 6");
      } catch (GedcomException e) {
        
      }

  }
  
  
  public void testCircleThroughHUSB() {
    
    gedcom.deleteEntity(indi(6));
    int fam = 3;
    int[] husbandsToCheck = { 2, 0 };
    for (int i=0; i<husbandsToCheck.length; i++)
      try {
        link(fam(fam), "HUSB", husbandsToCheck[i]);
        fail("didn't recognize circle through HUSB between family "+fam+" and indi "+husbandsToCheck[i]);
      } catch (GedcomException e) {
        
      }
    
  }
  
  
  public void testCircleThroughWIFE() {
    
    gedcom.deleteEntity(indi(13));
    int fam = 6;
    int[] wifesToCheck = { 5, 1 };
    for (int i=0; i<wifesToCheck.length; i++)
      try {
        link(fam(fam), "WIFE", wifesToCheck[i]);
        fail("didn't recognize circle through WIFE between family "+fam+" and indi "+wifesToCheck[i]);
      } catch (GedcomException e) {
        
      }
  }  
  
}