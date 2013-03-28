
package validate;

import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.view.ViewContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestCardinality extends Test {

  
  public TestCardinality() {
    super((String[])null, Property.class);
  }

  
  void test(Property prop, TagPath path, List issues, ReportValidate report) {
    
    MetaProperty itsmeta = prop.getMetaProperty();

    
    Map seen = new HashMap();
    for (int i=0,j=prop.getNoOfProperties(); i<j ; i++) {
      Property child = prop.getProperty(i);
      String tag = child.getTag();
      MetaProperty meta = itsmeta.getNested(tag, false); 
      if (meta.isSingleton()) {
        if (!seen.containsKey(tag))
          seen.put(tag, child);
        else {
          Property first = (Property)seen.get(tag);
          if (first!=null) {
            seen.put(tag, null);
            issues.add(new ViewContext(first).setText(report.translate("err.cardinality.max", prop.getTag(), first.getTag(), prop.getGedcom().getGrammar().getVersion(), meta.getCardinality() )));
          }
        }
      }
    }
    
    
    MetaProperty[] metas = prop.getNestedMetaProperties(0);
    for (int i = 0; i < metas.length; i++) {
      if (metas[i].isRequired() && seen.get(metas[i].getTag())==null) {
        String txt = report.translate("err.cardinality.min", prop.getTag(), metas[i].getTag(), prop.getGedcom().getGrammar().getVersion(), metas[i].getCardinality() );
        issues.add(new ViewContext(prop).setImage(metas[i].getImage()).setText(txt));
      }
    }

    
  }

} 