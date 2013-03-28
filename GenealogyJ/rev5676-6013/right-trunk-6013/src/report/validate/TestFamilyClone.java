
package validate;

import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestFamilyClone extends Test {
  
  
  private Set reportedFams = new HashSet();

  
  public TestFamilyClone() {
    super("FAM", Property.class);
  }

  
  void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    
    Fam fam = (Fam)prop;
    if (reportedFams.contains(fam))
      return;
    
    
    Indi husband = fam.getHusband();
    Indi wife = fam.getWife();

    if (husband!=null)
      test(fam, husband.getFamiliesWhereSpouse(), issues, report);
    else if (wife!=null)
      test(fam, wife.getFamiliesWhereSpouse(), issues, report);
  }
  
  private void test(Fam fam, Fam[] others, List issues, ReportValidate report) {

    for (int i = 0; i < others.length; i++) {
      Fam other = others[i];
      if (fam==other) continue;
      if (isClone(fam, other)) {
        if (!reportedFams.contains(fam)) {
          issues.add(new ViewContext(fam).setText(report.translate("warn.fam.cloned", fam.getId() )));
          reportedFams.add(fam);
        }
        issues.add(new ViewContext(other).setText(report.translate("warn.fam.clone", other.getId(), fam .getId() )));
        reportedFams.add(other);
      }
    }

  }
  
  private boolean isClone(Fam fam, Fam other) {
    
    if (fam.getHusband()!=other.getHusband())
      return false;
    if (fam.getWife()!=other.getWife())
      return false;
    
    if (fam.getProperty("DIV")!=null||other.getProperty("DIV")!=null)
      return false;
    
    return true;
  }

} 