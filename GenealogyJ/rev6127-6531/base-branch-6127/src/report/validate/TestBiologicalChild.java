
package validate;

import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyFamilyChild;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class TestBiologicalChild extends Test {

  
   TestBiologicalChild() {
    
    super("INDI", Indi.class);
  }
  
  
   void test(Property prop, TagPath trigger, List issues, ReportValidate report) {

    
    List famcs = prop.getProperties(PropertyFamilyChild.class);
    for (ListIterator it = famcs.listIterator(); it.hasNext(); ) {
      PropertyFamilyChild famc = (PropertyFamilyChild)it.next();
      if (famc.isValid() && Boolean.FALSE.equals(famc.isBiological()))
        it.remove();
    }
    
    
    if (famcs.size()>1) for (Iterator it = famcs.iterator(); it.hasNext() ;) {
      issues.add(new ViewContext((Property)it.next()).setText(report.translate("warn.famc.biological")));
    }
    
    
  }
  

} 