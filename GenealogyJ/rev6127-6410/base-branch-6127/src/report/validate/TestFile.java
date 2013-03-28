
package validate;

import genj.gedcom.Property;
import genj.gedcom.PropertyFile;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.List;


public class TestFile extends Test {

  
  public TestFile() {
    super((String[])null, PropertyFile.class);
  }

  
  void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    
    PropertyFile file = (PropertyFile)prop;
    
    
    if (file.getFile()==null) 
      issues.add(new ViewContext(prop).setText(report.translate("err.nofile")));

  }

} 