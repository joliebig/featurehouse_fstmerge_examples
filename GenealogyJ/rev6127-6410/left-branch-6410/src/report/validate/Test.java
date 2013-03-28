
package validate;

import genj.gedcom.Property;
import genj.gedcom.TagPath;

import java.util.List;


 abstract class Test {
  
  private TagPath[] pathTriggers;
  
  private Class typeTrigger;

  
   Test(String pathTrigger, Class typeTrigger) {
    this(new String[]{pathTrigger}, typeTrigger);
  }

  
   Test(String[] pathTriggers, Class typeTrigger) {
    if (pathTriggers!=null) {
      this.pathTriggers = new TagPath[pathTriggers.length];
      for (int i=0;i<pathTriggers.length;i++)
        this.pathTriggers[i] = new TagPath(pathTriggers[i]);
    }
    this.typeTrigger = typeTrigger;
  }
  
  
   boolean applies(Property prop, TagPath path) {
    
    outer: while (pathTriggers!=null) {
      for (int j=0;j<pathTriggers.length;j++) {
        if (pathTriggers[j].equals(path)) break outer;
      }
      return false;
    }
    
    return typeTrigger==null||typeTrigger.isAssignableFrom(prop.getClass());
  }
  
  
   abstract void test(Property prop, TagPath path, List issues, ReportValidate report);
   

} 