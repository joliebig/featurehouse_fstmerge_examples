
package genj.gedcom;

import genj.gedcom.time.PointInTime;
import genj.util.Origin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class PropertyTest extends TestCase {

  private Gedcom gedcom;
  
  
  protected void setUp() throws Exception {

    
    gedcom = new Gedcom(Origin.create("file://foo.ged"));

    
  }
  
  
  private Indi createIndi() {
    
    Indi indi = null;
    
    try {
	    
	    indi = (Indi)gedcom.createEntity("INDI");
	    
	    indi.addDefaultProperties();
    } catch (GedcomException e) {
      fail(e.getMessage());
    }
    
    
    return indi;
  }
  
  
  public void testMove() throws GedcomException {
    
    final Indi indi = createIndi();
    
    
    indi.delProperties();
    final List list = new ArrayList();
    for (int i=0; i<10; i++) {
      list.add(indi.addProperty("foo", ""+i));
    }
    
    gedcom.doUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        
        
        for (int i=0;i<10;i++) 
          indi.moveProperty(0, 10-i);
        assertProperties(indi, new int[]{ 9,8,7,6,5,4,3,2,1,0 });

        
        for (int i=0;i<10;i++) 
          indi.moveProperty(9, i);
        assertProperties(indi, new int[]{ 0,1,2,3,4,5,6,7,8,9});
        
        
        indi.moveProperties(list.subList(0, 5), 10);
        assertProperties(indi, new int[]{ 5,6,7,8,9,0,1,2,3,4});
        
        
        indi.moveProperties(list.subList(0, 5), 0);
        assertProperties(indi, new int[]{ 0,1,2,3,4,5,6,7,8,9});
        
      }
    });

    
    gedcom.undoUnitOfWork();
    assertProperties(indi, new int[]{ 0,1,2,3,4,5,6,7,8,9});
    
    
    gedcom.doUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        indi.moveProperty(0, 2);
        assertProperties(indi, new int[]{ 1,0,2,3,4,5,6,7,8,9});
      }
    });
    
    
    gedcom.undoUnitOfWork();
    assertProperties(indi, new int[]{ 0,1,2,3,4,5,6,7,8,9});
    
    
    gedcom.doUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        indi.moveProperty(1, 0);
        assertProperties(indi, new int[]{ 1,0,2,3,4,5,6,7,8,9});
      }
    });
    
    
    gedcom.undoUnitOfWork();
    assertProperties(indi, new int[]{ 0,1,2,3,4,5,6,7,8,9});
  }
  
  private void assertProperties(Property parent, int[] children) {
    parent.delProperties("CHAN");
    assertTrue(parent.getNoOfProperties()==children.length);
    for (int i = 0; i < children.length; i++) {
      assertEquals(Integer.parseInt(parent.getProperty(i).getValue()), children[i]);
    }
  }
  
  
  public void testAdd() throws GedcomException {     

    
    final Indi indi = createIndi();
    Property[] before = indi.getProperties();
    
    
    final PropertyName name = new PropertyName();
    final int pos = 1;

    Tracker tracker = new Tracker();
    gedcom.addGedcomListener(tracker);
    
    gedcom.doUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        indi.addProperty(name, pos);
        indi.delProperty(indi.addProperty("FOO", "bar"));
      }
    });
    
    
    Property[] afterEdit = indi.getProperties();
    
    
    
    
    
    assertEquals("wrong # of changes", 4, tracker.changes);
    assertEquals("expected change add/NAME", indi.getProperty(1), tracker.propertiesAdded.get(0));
    assertEquals("expected change add/CHAN", "CHAN", indi.getProperty(indi.getNoOfProperties()-1).getTag());
    
    
    assertEquals("wrong # of properties", before.length+2, afterEdit.length);
    assertEquals("expected NAME/"+pos, afterEdit[pos], name);
    assertEquals("expected CHAN/"+pos, afterEdit[afterEdit.length-1].getTag(), "CHAN");
    
    
    gedcom.undoUnitOfWork();
    
    
    Property[] afterUndo = indi.getProperties();
    assertEquals("undo didn't revert edits", Arrays.asList(afterUndo), Arrays.asList(before));
    
    
    gedcom.redoUnitOfWork();
    
    
    Property[] afterRedo = indi.getProperties();
    assertEquals("redo  didn't restore undo", Arrays.asList(afterRedo), Arrays.asList(afterEdit));
    
    
    gedcom.undoUnitOfWork();
    
    
    afterUndo = indi.getProperties();
    assertEquals("undo didn't restore redo", Arrays.asList(afterUndo), Arrays.asList(before));
    
    
  }
  
  private class Tracker implements GedcomListener {
    
    int changes = 0;
    List entitiesAdded = new ArrayList();
    List entitiesDeleted = new ArrayList();
    List propertiesAdded = new ArrayList();
    List propertiesDeleted = new ArrayList();
    List propertiesChanged = new ArrayList();

    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      changes++;
      entitiesAdded.add(entity);
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      changes++;
      entitiesDeleted.add(entity);
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      changes++;
      propertiesAdded.add(added);
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      changes++;
      propertiesChanged.add(property);
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      changes++;
      propertiesDeleted.add(removed);
    }
    
  }
  
  
  public void testPrivacyPolicy() {     
    
    Options.getInstance().maskPrivate = "xxx";

    Indi indi = createIndi();
    
    Options options = Options.getInstance();
    options.dateFormat = PointInTime.FORMAT_GEDCOM;
    
    Property birt = indi.addProperty("BIRT", "");
    Property date = birt.addProperty("DATE", "25 MAY 1970");
    birt.addProperty("PLAC", "Rendsburg");
    
    
    assertEquals("born 25 MAY 1970, Rendsburg", birt.format("born{ $D}{, $P}", PrivacyPolicy.PUBLIC));
    
    
    assertEquals("born 25 MAY 1970, Rendsburg", birt.format("born{ $D}{, $P}", new PrivacyPolicy(false, 10, "_SECRET")));
    
    
    assertEquals("born xxx, xxx", birt.format("born{ $D}{, $P}", new PrivacyPolicy(false, Integer.MAX_VALUE, "_SECRET")));

    
    date.addProperty("_SECRET", "");
    assertEquals("born xxx Rendsburg", birt.format("born{ $D}{ $P}", new PrivacyPolicy(false, 0, "_SECRET")));

    
    birt.addProperty("_SECRET", "");
    assertEquals("born 25 MAY 1970, Rendsburg", birt.format("born{ $D}{, $P}", PrivacyPolicy.PUBLIC));
    assertEquals("born xxx, xxx", birt.format("born{ $D}{, $P}", new PrivacyPolicy(false, 0, "_SECRET")));
    
    
    assertEquals("born xxx", birt.format("born{ $D}{ $P}", new PrivacyPolicy(false, 0, "_SECRET")));

    
    indi.addProperty("DEAT", "").addProperty("DATE", "(im Hohen Alter)");
    assertEquals("born xxx in xxx", birt.format("born{ $D}{ in $P}", new PrivacyPolicy(false, Integer.MAX_VALUE, null)));
    assertEquals("born xxx", birt.format("born{ $D}{ $P}", new PrivacyPolicy(false, Integer.MAX_VALUE, null)));
    assertEquals("born 25 MAY 1970, Rendsburg", birt.format("born{ $D}{, $P}", new PrivacyPolicy(true, Integer.MAX_VALUE, null)));

    
  }
  
  
  public void testFormatting() {     
    
    Options options = Options.getInstance();
    options.dateFormat = PointInTime.FORMAT_GEDCOM;
    
    Indi indi = createIndi();
    
    assertFormatted(indi, "BIRT", "", "25 MAY 1970", "Rendsburg, SH", "geboren{ am $D}{ in $P}", "geboren am 25 MAY 1970 in Rendsburg, SH");
    assertFormatted(indi, "BIRT", "", null                 , "Rendsburg, SH", "geboren{ am $D}{ in $P}", "geboren in Rendsburg, SH");
    assertFormatted(indi, "BIRT", "", null                 , "Rendsburg, SH", "geboren{ am $D}{ in $p}", "geboren in Rendsburg");
    assertFormatted(indi, "BIRT", "", null                 , null                    , "geboren{ am $D}{ in $p}", "");
    assertFormatted(indi, "BIRT", "", "25 MAY 1970", ""                        , "born {$y}{ in $P}", "born 1970");
    assertFormatted(indi, "BIRT", "", ""                     , ""                        , "born {$y}{ in $P}", "");
    assertFormatted(indi, "OCCU", "Pilot", null        , null                    , "{$V}{ in $p}", "Pilot");
    assertFormatted(indi, "OCCU", "Pilot", null        , "Ottawa"            , "{$V}{ in $p}", "Pilot in Ottawa");
    assertFormatted(indi, "OCCU", ""      , null         , "Ottawa"            , "{$V }{in $p}", "in Ottawa");
    assertFormatted(indi, "OCCU", ""      , null         , "Ottawa"            , "Occupation: {$V}", "");
    assertFormatted(indi, "IMMI", ""      , null           , "Vancouver"       , "Immigration{ in $p (landed)}{ on $D}", "Immigration in Vancouver (landed)");
    
    assertFormatted(indi, "BIRT", "", "25 MAY 1970", "Rendsburg, SH", "{$T}{ $D}{ in $p}", Gedcom.getName("BIRT")+" 25 MAY 1970 in Rendsburg");
    assertFormatted(indi, "BIRT", "", ""                     , "Rendsburg, SH", "{$t}{ $D}{ $P}"    , "BIRT Rendsburg, SH");
    assertFormatted(indi, "BIRT", "", ""                     , ""                        , "{$T}{ $D}{ in $p}", "");
  }
  
  private void assertFormatted(Indi indi, String evenTag, String evenValue, String date, String place, String format, String result) {
    
    
    Property p = indi.addProperty(evenTag, evenValue);
    if (date!=null)
      p.addProperty("DATE", date);
    if (place!=null)
      p.addProperty("PLAC", place);
    
    
    assertEquals(result, p.format(format));
    
  }
    
} 
