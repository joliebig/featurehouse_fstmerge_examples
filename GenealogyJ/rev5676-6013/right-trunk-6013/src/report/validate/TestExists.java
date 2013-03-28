
package validate;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.List;


 class TestExists extends Test {

  
  private TagPath path1;

  
  private TagPath path2;

  
   TestExists(String trigger, String path1, String path2) {
    
    super(trigger, null);
    
    this.path1 = new TagPath(path1);
    this.path2 = new TagPath(path2);
  }

  
   void test(Property prop, TagPath trigger, List issues, ReportValidate report) {

    Entity entity = prop.getEntity();

    
    Property prop1 = prop.getProperty(path1);
    if (prop1==null)
      return;
    
    
    Property prop2 = prop.getProperty(path2);
    if (prop2!=null)
      return;

    
    String text = report.translate("err.exists.without", Gedcom.getName(prop1.getTag()), Gedcom.getName(path2.getLast()) );
    issues.add(new ViewContext(prop1).setText(text));
    
  }
  
} 