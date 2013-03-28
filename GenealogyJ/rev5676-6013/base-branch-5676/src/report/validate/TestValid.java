
package validate;

import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.List;


 class TestValid extends Test {

  
  private ReportValidate report;
  
  
   TestValid(ReportValidate report) {
    super((String[])null, Property.class);
    this.report = report;
  }
  
  
   void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    
    if (!report.isPrivateValueValid&&prop.isPrivate()) {
      
      issues.add(new ViewContext(prop).setText(report.translate("err.private", path.toString())));
    }

    
    if (prop.isValid())
      return;
      
    
    if (report.isEmptyValueValid&&prop.getValue().length()==0)
      return;
      
    
    issues.add(new ViewContext(prop).setText(report.translate("err.notvalid", path.toString())));
    
    
  }

} 