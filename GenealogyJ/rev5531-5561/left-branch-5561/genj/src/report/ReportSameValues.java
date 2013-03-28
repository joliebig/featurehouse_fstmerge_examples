
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyChoiceValue;
import genj.gedcom.PropertyName;
import genj.report.AnnotationsReport;


public class ReportSameValues extends AnnotationsReport {

  
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

  
  public void start(PropertyChoiceValue choice) {
    find(choice.getGedcom(), choice.getPropertyName(), choice.getSameChoices(), choice.getDisplayValue());
  }

  
  public void start(PropertyName name) {
    find(name.getGedcom(), name.getPropertyName(), name.getSameLastNames(), name.getLastName());
  }

  
  private void find(Gedcom gedcom, String propName, Property[] sameProps, String val) {

    if (val==null||val.length()==0)
      return;

    
    for (int i=0; i<sameProps.length; i++) {

      
      Property prop = sameProps[i];
      Property parent = prop.getParent();

      String txt;
      if (parent instanceof Entity)
        txt = prop.getEntity().toString();
      else
        txt = parent.getPropertyName() + " | " +prop.getEntity();

      
      addAnnotation(prop, txt);
    }

    
    sortAnnotations();

    
    setMessage(translate("xname",new String[]{ propName, val}));

    
  }

} 