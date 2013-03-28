
package validate;

import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyPlace;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.List;


public class TestPlace extends Test {
  
  private String globalHierarchy;
  
  
   TestPlace(Gedcom gedcom) {
    super((String[])null, PropertyPlace.class);
    globalHierarchy = gedcom.getPlaceFormat();
  }

  
   void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    PropertyPlace place = (PropertyPlace)prop;
    
    
    String hierarchy = place.getFormatAsString(); 
    if (!hierarchy.equals(globalHierarchy)) {
      issues.add(new ViewContext(place).setText(report.translate("warn.plac.format")));
    }
    
    
    if (hierarchy.length()>0 && (place.getValue().length()>0 || !report.isEmptyValueValid)) {
      String[] jurisdictions = place.getJurisdictions();
      String[] format = place.getFormat();
      if (format.length!=jurisdictions.length) {
        issues.add(new ViewContext(place).setText(report.translate("warn.plac.value", String.valueOf(jurisdictions.length), String.valueOf(format.length))));
      }
    }

    
  }

}
