
package validate;

import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.List;


 class TestSpouseGender extends Test {

  
   TestSpouseGender() {
    super(new String[]{"FAM"}, Property.class);
  }
  
  
   void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    
    Fam fam = (Fam)prop;
    
    
    Indi husband = fam.getHusband();
    if (!testSex(husband, PropertySex.MALE))
      addIssue(issues, fam, "HUSB", report); 
      
    Indi wife = fam.getWife();
    if (!testSex(wife, PropertySex.FEMALE)) 
      addIssue(issues, fam, "WIFE", report); 

    
  }    

  
  private boolean testSex(Indi indi, int sex) {
    return indi==null ? true : indi.getSex()==sex;
  }

  
  private void addIssue(List issues, Fam fam, String role, ReportValidate report) {
    
    PropertyXRef xref = (PropertyXRef)fam.getProperty(role);
    Indi indi = (Indi)xref.getTargetEntity();
     
    String[] format = new String[] {
      Gedcom.getName(role),
      indi.toString()
    };
    
    issues.add(new ViewContext(xref).setText(report.translate("err.spouse."+role, format)));
  }

} 