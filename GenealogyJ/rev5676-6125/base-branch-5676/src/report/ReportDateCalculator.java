
import genj.gedcom.GedcomException;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.Calendar;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.report.Report;

import javax.swing.JLabel;

public class ReportDateCalculator extends Report {

	  public String accepts(Object context) {

	
	String val = null;
	if (context instanceof PropertyDate) {
	    if (((Property)context).getParent().getTag().equals("BIRT")){
		return translate("xname.evt");
	    } else {
		return translate("xname.birt");
	    }
	    
	    
	} else {
	    return null;
	}
    }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(PropertyDate date) {
      
      
      
      
      PointInTime pit = date.getStart();
      String result;
      if (pit == null){
	  result = translate("date.undef");
      } else {
	  try {
	      pit = PointInTime.getPointInTime(pit.getTimeMillis());
	  } catch (GedcomException e) {
	  }
	  Property parent = date.getParent();
	  String ageStr;
	  if (parent.getProperty("AGE") != null){
	      ageStr = parent.getProperty("AGE").getValue();
	  } else {
	      ageStr = getValueFromUser( translate("date.title"), translate("age.title"), new String[0]);
        
        if (ageStr==null)
          return;

	  }

	  Delta age = new Delta(0,0,0);
	  if (age.setValue(ageStr) || age.setValue(ageStr+"y")){
	      if (date.getParent().getTag().equals("BIRT")){
		  PointInTime calcDate = getDateFromDateAndAge(pit,age,1);
		  result = translate("date.evt.label",calcDate.toString());
	      } else {
		  PointInTime calcDate = getDateFromDateAndAge(pit,age,-1);
		  result = translate("date.birth.label",calcDate.toString());
	      }
	  } else {
	      result = translate("age.invalid");
	  }
      }
      showComponentToUser(new JLabel(result));
  }

    
    private static PointInTime getDateFromDateAndAge(PointInTime date, Delta age, int op) {

    
    if (date==null||age==null)
      return null;

    
    if (!date.isValid())
      return null;

    
    Calendar calendar = date.getCalendar();
    if (calendar!=age.getCalendar())
      return null;

    
    int
      y =  date.getYear (),
      m = date.getMonth(),
      d = date.getDay();

    
    if (date.getYear()==PointInTime.UNKNOWN)
	return null;
    int year  = date.getYear() + op * age.getYears();
    int month = date.getMonth();
    int day = date.getDay();
    if (date.getMonth()!=PointInTime.UNKNOWN){
	
	month += op*age.getMonths();

	
	if (day!=PointInTime.UNKNOWN){
	    
	    day += op*age.getDays();
	}
    }
    
    return normalize(new PointInTime(day, month, year, calendar));
  }

    private static PointInTime normalize(PointInTime pit){
	int year=pit.getYear();
	int month=pit.getMonth();
	int day=pit.getDay();
	Calendar cal = pit.getCalendar();

	if (month == PointInTime.UNKNOWN)
	    return pit;
	if (month > cal.getMonths())
	    return normalize(new PointInTime(day,month-cal.getMonths(),year+1,cal));
	if (month <0 )
	    return normalize(new PointInTime(day,month+cal.getMonths(),year-1,cal));
	if (day == PointInTime.UNKNOWN)
	    return pit;
	if (day > cal.getDays(month,year))
	    return normalize(new PointInTime(day-cal.getDays(month,year),month+1,year,cal));
	if (day <0 )
	    return normalize(new PointInTime(day+cal.getDays(month-1,year),month-1,year,cal));
	return pit;
    }


} 