
package genj.gedcom.time;

import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.util.Resources;
import genj.util.swing.ImageIcon;

import java.util.HashMap;
import java.util.Map;



public abstract class Calendar {
  
  
  protected String escape;
  protected String name;
  protected ImageIcon image;
  protected String[] months;
  protected String[] monthsLowerCase;
  protected String[] weekDays, localizedWeekDays;
  protected Map<String,String>
    localizedMonthNames = new HashMap<String,String>(),
    abbreviatedMonthNames = new HashMap<String,String>();
    
  protected final static Resources resources = PointInTime.resources; 

  
  public final static String 
    TXT_CALENDAR_SWITCH = resources.getString("cal.switch"),
    TXT_CALENDAR_RESET  = resources.getString("cal.reset");

  
  protected Calendar(String esc, String key, String img, String[] mOnths, String[] weEkDays) {
    
    
    months = mOnths;
    monthsLowerCase = new String[months.length];
    for (int i = 0; i < months.length; i++) monthsLowerCase[i] = months[i].toLowerCase();
    escape = esc;
    name = resources.getString("cal."+key);
    image = new ImageIcon(Gedcom.class, img);
    
    
    weekDays = weEkDays;
    localizedWeekDays = new String[weekDays.length];
    for (int wd=0;wd<weekDays.length;wd++)
      localizedWeekDays[wd] = resources.getString("day."+weekDays[wd]);
    
    
    for (int m=0;m<months.length;m++) {

      
      String mmm = months[m];

      
      String localized = resources.getString("mon."+mmm);
  
      
      
      
      
      String abbreviated;

      int marker = localized.indexOf('|'); 
      if (marker>0) {
        abbreviated = localized.substring(0, marker);
        localized = abbreviated + localized.substring(marker+1);
      } else {
        marker = localized.indexOf(',');
        if (marker>0) {
          abbreviated = localized.substring(marker+1);
          localized = localized.substring(0, marker);
        } else {
          abbreviated = localized.length()>3 ? localized.substring(0,3) : localized;
        }
      }
  
      
      localizedMonthNames.put(mmm, localized);
      abbreviatedMonthNames.put(mmm, abbreviated);
  
      
    }  

    
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public ImageIcon getImage() {
    return image;
  }
  
  
  protected int parseMonth(String mmm) throws NumberFormatException {
    
    String mmmLowerCase = mmm.toLowerCase();
    for (int i=0;i<months.length;i++) {
      if (monthsLowerCase[i].equals(mmmLowerCase)) return i;
    }
    throw new NumberFormatException();
  }
  
  
  public String getDay(int day) {
    if (day==PointInTime.UNKNOWN)
      return "";
    return ""+(day+1);
  }
  
  
  public String[] getMonths(boolean localize) {
    
    String[] result = new String[months.length];
    for (int m=0;m<result.length;m++) {
      String mmm = months[m];
      if (localize) 
        mmm = localizedMonthNames.get(mmm).toString();
      result[m] = mmm;
    }
    return result;
  }

  
  public String getMonth(int month) {
    
    if (month<0||month>=months.length)
      return "";
    
    return months[month];
  }
  
  public String getDisplayMonth(int month, boolean abbrev) {
    String mmm = getMonth(month);
    if (mmm.length()==0)
      return mmm;
    return abbrev ? abbreviatedMonthNames.get(mmm).toString() : localizedMonthNames.get(mmm).toString();
  }
  
  
  public String getYear(int year) {
    if (year==PointInTime.UNKNOWN)
      return "";
    return ""+year;
  }
  
  public String getDisplayYear(int year) {
    return getYear(year);
  }
  
  
  public int getYear(String year) throws GedcomException {
    try {
      return Integer.parseInt(year);
    } catch (NumberFormatException e) {
      throw new GedcomException(resources.getString("year.invalid"));
    }
  }

  
  public int getMonths() {
    return months.length;
  }
  
  
  public abstract int getDays(int month, int year);
      
  
  protected String getDayOfWeek(PointInTime pit, boolean localize) throws GedcomException {
    if (!pit.isComplete())
      throw new GedcomException(resources.getString("pit.incomplete"));
    String[] result = localize ? localizedWeekDays : localizedWeekDays;
    int dow = (pit.getJulianDay() + 1) % 7;
    return result[dow >= 0 ? dow : dow+7];
  }
  
  
  protected final int toJulianDay(PointInTime pit) throws GedcomException {

    
    int 
      year  = pit.getYear () ,
      month = pit.getMonth(),
      day   = pit.getDay  ();
      
    
    if (year==PointInTime.UNKNOWN||year==0)
      throw new GedcomException(resources.getString("year.invalid"));
      
    
    if (month==PointInTime.UNKNOWN&&day!=PointInTime.UNKNOWN)
      throw new GedcomException(resources.getString("month.invalid"));
      
    
    if (month==PointInTime.UNKNOWN)
      month = 0;
    else if (month<0||month>=months.length)
      throw new GedcomException(resources.getString("month.invalid"));

    
    if (day==PointInTime.UNKNOWN)
      day = 0;
    else if (day<0||day>=getDays(month,year))
      throw new GedcomException(resources.getString("day.invalid"));

    
    return toJulianDay(day, month, year);
  }
  
  
  protected abstract int toJulianDay(int day, int month, int year) throws GedcomException;
  
  
  protected abstract PointInTime toPointInTime(int julianDay) throws GedcomException;

  
  public String toString() {
    return getName();
  }
  
} 