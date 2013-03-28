
package validate;

import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.TagPath;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.util.WordBuffer;
import genj.view.ViewContext;

import java.util.List;


public class TestAge extends Test {

  
   final static int 
    OVER = 0,
    UNDER = 1;
    
  
  private TagPath path2date;    
    
  
  private TagPath path2indi;    
    
  
  private int comparison;
  
  
  private int years;

  
  private String explanation;

  
   TestAge(String trigger, String p2indi, int comp, int yrs, String expltn) {
    this(trigger, null, p2indi, comp, yrs, expltn);
  }

  
   TestAge(String trigger, String p2date, String p2indi, int comp, int yrs, String expltn) {
    
    super(trigger, p2date!=null?Property.class:PropertyDate.class);
    
    explanation = expltn;
    path2date = p2date!=null?new TagPath(p2date):null;
    path2indi = new TagPath(p2indi);
    comparison = comp;
    years = yrs;
  }
  
  
   void test(Property prop, TagPath trigger, List issues, ReportValidate report) {
    
    
    PropertyDate date ;
    if (path2date!=null) {
      date = (PropertyDate)prop.getProperty(path2date);
    } else {
      date = (PropertyDate)prop;
    }
      
    if (date==null||!date.isValid())
      return;

    
    Property pindi = prop.getProperty(path2indi);
    if (!(pindi instanceof Indi))
      return;
    Indi indi = (Indi)pindi;      

    
    PointInTime pit2 = date.getStart();

    
    PropertyDate birt = indi.getBirthDate();
    if (birt==null||!birt.isValid())
      return;
    PointInTime pit1 = birt.getStart();
    
    
    if (pit1.compareTo(pit2)>0)
      return;
    
    
    Delta delta = Delta.get(pit1, pit2);
    if (delta==null)
      return;
      
    
    if (isError(delta.getYears()))  {
      
      WordBuffer words = new WordBuffer();
      String[] format = new String[]{ indi.toString(), String.valueOf(years)}; 
      if (comparison==UNDER) {
        words.append(report.translate("err.age.under", format));
      } else {
        words.append(report.translate("err.age.over", format));
      }
      words.append("-");
      words.append(report.translate(explanation));

      issues.add(new ViewContext(prop).setText(words.toString()).setImage(prop instanceof PropertyDate ? prop.getParent().getImage(false) : prop.getImage(false)));
    }
    
    
  }
  
  
  private boolean isError(int age) {
    switch (comparison) {
      case OVER:
        return age > years;
      case UNDER:
        return age < years;
    }
    return false;
  }

} 