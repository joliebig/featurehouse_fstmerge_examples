
package validate;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.Submitter;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.report.AnnotationsReport;
import genj.util.EnvironmentChecker;
import genj.util.swing.Action2;
import genj.view.ViewContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ReportValidate extends AnnotationsReport {

  
  public boolean isOrderDiscretionary = true;

  
  public boolean isEmptyValueValid = true;

  
  public boolean isPrivateValueValid = true;

  
  public boolean isFileNotFoundValid = true;

  
  public boolean isUnderscoreValid = true;

  
  public boolean isExtramaritalValid = false;

  
  public boolean isRelaxedPlaceFormat = false;

  
  public int
    maxLife      = 95,
    minAgeMARR   = 15,
    maxAgeBAPM   =  6,
    minAgeRETI   = 45,
    minAgeFather = 14,
    minAgeMother = 16,
    maxAgeMother = 44;

  

  private final static String[] LIFETIME_DATES = {
    "INDI:ADOP:DATE",
    "INDI:ADOP:DATE",
    "INDI:BAPM:DATE",
    "INDI:BAPL:DATE",
    "INDI:BARM:DATE",
    "INDI:BASM:DATE",
    "INDI:BLES:DATE",
    "INDI:CHRA:DATE",
    "INDI:CONF:DATE",
    "INDI:ORDN:DATE",
    "INDI:NATU:DATE",
    "INDI:EMIG:DATE",
    "INDI:IMMI:DATE",
    "INDI:CENS:DATE",
    "INDI:RETI:DATE"
  };

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Property[] props) {

    List issues = new ArrayList();

    if (props.length>0) {
      List tests = createTests(props[0].getGedcom());

      for (int i=0;i<props.length;i++) {
        TagPath path = props[i].getPath();
        test(props[i], path, props[i].getGedcom().getGrammar().getMeta(path), tests, issues);
      }
    }

    
    results(issues);
  }

  
  public void start(Entity entity) {
    start(new Entity[]{ entity });
  }

  public void start(Entity[] entities) {

    Gedcom gedcom = entities[0].getGedcom();
    List tests = createTests(gedcom);

    List issues = new ArrayList();
    for (int i=0;i<entities.length;i++) {
      TagPath path = new TagPath(entities[i].getTag());
      test(entities[i], path, entities[i].getGedcom().getGrammar().getMeta(path), tests, issues);
    }

    
    results(issues);
  }

  
  public void start(final Gedcom gedcom) {

    
    List tests = createTests(gedcom);
    List issues = new ArrayList();

    
    if (gedcom.getSubmitter()==null) {
      final ViewContext ctx = new ViewContext(gedcom);
      ctx.setText(translate("err.nosubmitter", gedcom.getName())).setImage(Gedcom.getImage());
      ctx.addAction(new Action2(translate("fix")) {
        protected void execute() {
          setEnabled(false);
          gedcom.doMuteUnitOfWork(new UnitOfWork() {
            public void perform(Gedcom gedcom) throws GedcomException {
              Submitter sub = (Submitter)gedcom.createEntity(Gedcom.SUBM);
              sub.setName(EnvironmentChecker.getProperty(ReportValidate.this, "user.name", "?", "using user.name for fixing missing submitter"));
            }
          });
        }
      });
      issues.add(ctx);
    }

    
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      for (Iterator es=gedcom.getEntities(Gedcom.ENTITIES[t]).iterator();es.hasNext();) {
        Entity e = (Entity)es.next();
        TagPath path = new TagPath(e.getTag());
        test(e, path, gedcom.getGrammar().getMeta(path), tests, issues);
      }
    }

    
    results(issues);
  }

  
private void results(List<Context> issues) {

    
    if (issues.size()==0) {
      setMessage(translate("noissues"));
      return;
    }

    
    setMessage(translate("issues", Integer.toString(issues.size())));
    for (Context ctx : issues)
    	addAnnotation(ctx);

    
  }

  
  private void test(Property prop, TagPath path, MetaProperty meta, List tests, List issues) {
    
    for (int i=0, j=tests.size(); i<j; i++) {
      Test tst = (Test)tests.get(i);
      
      if (!tst.applies(prop, path))
        continue;
      
      tst.test(prop, path, issues, this);
      
    }
    
    if (isUnderscoreValid&&prop.getTag().startsWith("_"))
      return;
    
    for (int i=0,j=prop.getNoOfProperties();i<j;i++) {
      
      Property child = prop.getProperty(i);
      if (child.isTransient())
        continue;
      
      String ctag = child.getTag();
      
      if (isUnderscoreValid&&ctag.startsWith("_"))
        continue;
      
      if (!meta.allows(ctag)) {
        String msg = translate("err.notgedcom", new String[]{ctag, prop.getGedcom().getGrammar().getVersion(), path.toString()});
        issues.add(new ViewContext(child).setText(msg).setImage(MetaProperty.IMG_ERROR));
        continue;
      }
      
      test(child, new TagPath(path, ctag), meta.getNested(child.getTag(), false), tests, issues);
      
    }
    
  }

  
  private List createTests(Gedcom gedcom) {

    List result = new ArrayList();

    

    
    result.add(new TestCardinality());

    
    result.add(new TestUniqueIDs());

    
    result.add(new TestValid(this));

    
    result.add(new TestSpouseGender());

    
    if (!isFileNotFoundValid)
      result.add(new TestFile());

    result.add(new TestFamilyClone());

    result.add(new TestBiologicalChild());

    
    if (!isOrderDiscretionary)
      result.add(new TestOrder("INDI", "FAMS", "FAMS:*:..:MARR:DATE"));

    
    if (!isOrderDiscretionary)
      result.add(new TestOrder("FAM", "CHIL", "CHIL:*:..:BIRT:DATE"));

    
    if (!isRelaxedPlaceFormat)
      result.add(new TestPlace(gedcom));

    

    
    result.add(new TestDate("INDI:BIRT:DATE",TestDate.AFTER  ,"INDI:DEAT:DATE"));

    
    result.add(new TestDate("INDI:BURI:DATE",TestDate.BEFORE ,"INDI:DEAT:DATE"));

    
    result.add(new TestDate(LIFETIME_DATES  ,TestDate.BEFORE ,"INDI:BIRT:DATE"));

    
    result.add(new TestDate(LIFETIME_DATES  ,TestDate.AFTER  ,"INDI:DEAT:DATE"));

    
    result.add(new TestDate("FAM:DIV:DATE" ,TestDate.BEFORE ,"FAM:MARR:DATE"));

    
    result.add(new TestDate("FAM:MARR:DATE" ,TestDate.AFTER  ,"FAM:HUSB:*:..:DEAT:DATE"));
    result.add(new TestDate("FAM:MARR:DATE" ,TestDate.AFTER  ,"FAM:WIFE:*:..:DEAT:DATE"));
    result.add(new TestDate("FAM:MARR:DATE", TestDate.BEFORE , "FAM:HUSB:*:..:BIRT:DATE"));
    result.add(new TestDate("FAM:MARR:DATE", TestDate.BEFORE , "FAM:WIFE:*:..:BIRT:DATE"));

    
    result.add(new TestDate("FAM:CHIL"      ,"*:..:BIRT:DATE", TestDate.AFTER  ,"FAM:WIFE:*:..:DEAT:DATE"));

    
    if (!isExtramaritalValid) {
	    result.add(new TestDate("FAM:CHIL"      ,"*:..:BIRT:DATE", TestDate.BEFORE ,"FAM:MARR:DATE"));
	    result.add(new TestDate("FAM:CHIL"      ,"*:..:BIRT:DATE", TestDate.AFTER  ,"FAM:DIV:DATE"));
      result.add(new TestExists("FAM:CHIL", ".", "..:MARR"));
    }

    

    
    if (maxLife>0)
      result.add(new TestAge ("INDI:DEAT:DATE","..:..", TestAge.OVER ,   maxLife, "maxLife"  ));

    
    if (maxAgeBAPM>0)
      result.add(new TestAge ("INDI:BAPM:DATE","..:..", TestAge.OVER ,maxAgeBAPM,"maxAgeBAPM"));

    
    if (maxAgeBAPM>0)
      result.add(new TestAge ("INDI:CHRI:DATE","..:..", TestAge.OVER ,maxAgeBAPM,"maxAgeBAPM"));

    
    if (minAgeRETI>0)
      result.add(new TestAge ("INDI:RETI:DATE","..:..", TestAge.UNDER,minAgeRETI,"minAgeRETI"));

    
    if (minAgeMARR>0)
      result.add(new TestAge ("FAM:MARR:DATE" ,"..:..:HUSB:*:..", TestAge.UNDER  ,minAgeMARR,"minAgeMARR"));
    if (minAgeMARR>0)
      result.add(new TestAge ("FAM:MARR:DATE" ,"..:..:WIFE:*:..", TestAge.UNDER  ,minAgeMARR,"minAgeMARR"));

    
    if (minAgeMother>0)
      result.add(new TestAge ("FAM:CHIL", "*:..:BIRT:DATE" ,"..:WIFE:*:..", TestAge.UNDER,minAgeMother,"minAgeMother"));
    if (maxAgeMother>0)
      result.add(new TestAge ("FAM:CHIL", "*:..:BIRT:DATE" ,"..:WIFE:*:..", TestAge.OVER ,maxAgeMother,"maxAgeMother"));
    if (minAgeFather>0)
      result.add(new TestAge ("FAM:CHIL", "*:..:BIRT:DATE" ,"..:HUSB:*:..", TestAge.UNDER,minAgeFather,"minAgeFather"));


    
    return result;
  }

} 