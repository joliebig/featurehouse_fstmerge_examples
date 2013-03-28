
package validate;

import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestUniqueIDs extends Test {
  
  private final static String[] PATHS = { "INDI:RIN" };
  
  private Map path2id2first = new HashMap();

  
  public TestUniqueIDs() {
    super(PATHS, Property.class);
  }

  
  void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    
    Map id2first = (Map)path2id2first.get(path);
    if (id2first==null) {
      id2first = new HashMap();
      path2id2first.put(path, id2first);
    }
    
    
    String value =prop.getValue();
    if (!id2first.containsKey(value)) {
      id2first.put(value, prop);
      return;
    }
    
    
    Property first = (Property)id2first.get(value);
    if (first!=null) {
      issues.add(new ViewContext(first).setText(report.translate("err.notuniqueid", new String[] { first.getTag(), first.getValue() })));
      id2first.put(value, null);
    }
    
    
    issues.add(new ViewContext(prop).setText(report.translate("err.notuniqueid", new String[] { prop.getTag(), prop.getValue() })));
    
    
    
  }

} 