
package validate;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.TagPath;
import genj.gedcom.time.PointInTime;
import genj.util.WordBuffer;
import genj.view.ViewContext;

import java.util.List;


 class TestDate extends Test {

  
   final static int
    AFTER = 0,
    BEFORE = 1;

  
  private TagPath path1;

  
  private TagPath path2;

  
  private int comparison;

  
   TestDate(String trigger, int comp, String path2) {
    this(new String[]{trigger}, null, comp, path2);
  }

  
   TestDate(String trigger, String path1, int comp, String path2) {
    this(new String[]{trigger}, path1, comp, path2);
  }

  
   TestDate(String[] triggers, int comp, String path2) {
    this(triggers, null, comp, path2);
  }

  
   TestDate(String[] triggers, String path1, int comp, String path2) {
    
    super(triggers, path1==null?PropertyDate.class:Property.class);
    
    comparison = comp;
    
    this.path1 = path1!=null ? new TagPath(path1) : null;
    this.path2 = new TagPath(path2);
  }

  
   void test(Property prop, TagPath trigger, List issues, ReportValidate report) {

    Entity entity = prop.getEntity();
    PropertyDate date1;

    
    if (path1!=null) {
      date1 = (PropertyDate)prop.getProperty(path1);
    } else {
      date1 = (PropertyDate)prop;
    }
    if (date1==null)
      return;

    
    
    Property date2 = entity.getProperty(path2);
    if (!(date2 instanceof PropertyDate))
      return;

    
    if (isError(date1, (PropertyDate)date2)) {

      WordBuffer buf = new WordBuffer();

      String event1 = Gedcom.getName(trigger.get(trigger.length()-(trigger.getLast().equals("DATE")?2:1)));
      String event2 = Gedcom.getName(path2.get(path2.length()-2));
      
      if (comparison==BEFORE)
        buf.append(report.translate("err.date.before", event1, event2));
      else
        buf.append(report.translate("err.date.after", event1, event2));
      
      entity = date2.getEntity();
      if (entity instanceof Indi)
        buf.append(report.translate("err.date.of", entity.toString()));

      issues.add(new ViewContext(prop).setText(buf.toString()).setImage(prop instanceof PropertyDate ? prop.getParent().getImage(false) : prop.getImage(false)));
    }

    
  }

  
  private boolean isError(PropertyDate date1, PropertyDate date2) {

    
    if (!(date1.isComparable()&&date2.isComparable()))
      return false;

    
    PointInTime pit1, pit2;
    int sign;
    if (comparison==AFTER) {
      
      pit1 = date1.getStart();
      pit2 = date2.isRange() ? date2.getEnd() : date2.getStart();
      sign = 1;
    } else {
      
      pit1 = date1.isRange() ? date1.getEnd() : date1.getStart();
      pit2 = date2.getStart();
      sign = -1;
    }

    
    return pit1.compareTo(pit2)*sign>0;
  }

} 