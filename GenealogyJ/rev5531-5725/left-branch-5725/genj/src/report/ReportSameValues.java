
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyChoiceValue;
import genj.gedcom.PropertyName;
import genj.report.Report;
import genj.view.ViewContext;
import genj.view.ViewContext.ContextList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReportSameValues extends Report {

  
  public String accepts(Object context) {

    
    String val = null;
    if (context instanceof PropertyChoiceValue)
      val = ((PropertyChoiceValue)context).getValue();
    if (context instanceof PropertyName)
      val = ((PropertyName)context).getLastName();

    
    if (val==null||val.length()==0)
      return null;

    
    return translate("xname", new String[]{ ((Property)context).getPropertyName(), val } );
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public ContextList start(PropertyChoiceValue choice) {
    return find(choice.getGedcom(), choice.getPropertyName(), choice.getSameChoices(), choice.getDisplayValue());
  }

  
  public ContextList start(PropertyName name) {
    return find(name.getGedcom(), name.getPropertyName(), name.getSameLastNames(), name.getLastName());
  }

  
  private ContextList find(Gedcom gedcom, String propName, Property[] sameProps, String val) {

    if (val==null||val.length()==0)
      return null;

    
    ArrayList<ViewContext> result = new ArrayList<ViewContext>();
    for (int i=0; i<sameProps.length; i++) {

      
      Property prop = sameProps[i];
      Property parent = prop.getParent();

      String txt;
      if (parent instanceof Entity)
        txt = prop.getEntity().toString();
      else
        txt = parent.getPropertyName() + " | " +prop.getEntity();

      
      result.add(new ViewContext(prop).setText(txt));
    }

    
    Collections.sort(result);

    
    return new ContextList(gedcom, translate("xname",new String[]{ propName, val}), result);
  }

} 