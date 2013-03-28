

package genj.gedcom;

import genj.io.GedcomIOException;
import genj.io.GedcomWriter;

import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;


public class GedcomDelTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(GedcomDelTest.class);
    }

    private Fam createTestFamily() throws GedcomException, IOException
    {
        Gedcom gedcom = new Gedcom();
        
        gedcom.doUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) throws GedcomException {
            Indi husband = (Indi) gedcom.createEntity(Gedcom.INDI, "Ihusband");
            Indi wife = (Indi) gedcom.createEntity(Gedcom.INDI, "Iwife");
            Indi child = (Indi) gedcom.createEntity(Gedcom.INDI, "Ikid");
            Fam family = (Fam) gedcom.createEntity(Gedcom.FAM,"F1");
            family.setHusband(husband);
            family.setWife(wife);
            family.addChild(child);
          }
        });
        
        
        validate(gedcom);
        
        return (Fam)gedcom.getEntity("F1");
    }
    
    
    public void testDeleteEntity()  throws Exception{
        testDeleteKid(createTestFamily());
        testDeleteParent(createTestFamily() );
        testDeleteFamily(createTestFamily());
    }
    
    
    private static void validate(Gedcom gedcom) throws IOException
    {
        OutputStream sink = new OutputStream() {
            public void write(int arg0) {  }
        };
        
        GedcomWriter writer = new GedcomWriter(gedcom,"test",null,sink);                    
        writer.write(); 
    }

    private void testDeleteFamily(Fam fam) throws Exception {
        Gedcom gedcom = fam.getGedcom();

        gedcom.deleteEntity(fam);

        validate(gedcom);
    }

    private void testDeleteParent(Fam fam) throws Exception {
        Gedcom gedcom = fam.getGedcom();

        gedcom.deleteEntity(fam.getHusband());

        validate(gedcom);
    }

    private void testDeleteKid( Fam fam) throws Exception {
        Gedcom gedcom = fam.getGedcom();

        gedcom.deleteEntity(fam.getChild(0));

        validate(gedcom);
    }

}