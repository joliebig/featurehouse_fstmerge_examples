
package genj.gedcom.time;

import genj.gedcom.GedcomException;
import genj.gedcom.Options;
import genj.util.DirectAccessTokenizer;
import genj.util.Resources;
import genj.util.WordBuffer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class PointInTime implements Comparable {
  
  public final static int
    FORMAT_GEDCOM = 0,
    FORMAT_SHORT = 1,
    FORMAT_LONG = 2,
    FORMAT_NUMERIC = 3;

  
   final static Resources resources = Resources.get(PointInTime.class);

  
  public final static int 
    UNKNOWN = Integer.MAX_VALUE;
  
  
  public final static GregorianCalendar GREGORIAN = new GregorianCalendar();
  public final static    JulianCalendar JULIAN    = new    JulianCalendar();
  public final static    HebrewCalendar HEBREW    = new    HebrewCalendar();
  public final static   FrenchRCalendar FRENCHR   = new   FrenchRCalendar();
    
  public final static Calendar[] CALENDARS = { GREGORIAN, JULIAN, HEBREW, FRENCHR };
  
  
  protected Calendar calendar = GREGORIAN;
  
  
  private int 
    day   = UNKNOWN, 
    month = UNKNOWN, 
    year  = UNKNOWN,
    jd    = UNKNOWN;

  
  public PointInTime() {
  }

  
  public PointInTime(Calendar cal) {
    calendar = cal;
  }
  
  
  public PointInTime(int d, int m, int y) {
    this(d,m,y,GREGORIAN);
  }
  
  
  public PointInTime(int d, int m, int y, Calendar cal) {
    day = d;
    month = m;
    year = y;
    calendar = cal;
    jd = UNKNOWN;
  }
  
  
  public PointInTime(String yyyymmdd) throws GedcomException {
    
    if (yyyymmdd==null||yyyymmdd.length()!=8)
      throw new GedcomException(resources.getString("pit.noyyyymmdd", yyyymmdd));
    
    
    try {
      year  = Integer.parseInt(yyyymmdd.substring(0, 4));
      month = Integer.parseInt(yyyymmdd.substring(4, 6))-1;
      day   = Integer.parseInt(yyyymmdd.substring(6, 8))-1;
    } catch (NumberFormatException e) {
      throw new GedcomException(resources.getString("pit.noyyyymmdd", yyyymmdd));
    }

    
  }
    
  
  public Calendar getCalendar() {
    return calendar;
  }

  
  public int getYear() {
    return year;
  }
  
  
  public int getMonth() {
    return month;    
  }

  
  public int getDay() {
    return day;
  }
  
  
  public static PointInTime getNow() {
    java.util.Calendar now = java.util.Calendar.getInstance(); 
    return new PointInTime(
      now.get(java.util.Calendar.DATE) - 1,      
      now.get(java.util.Calendar.MONTH),      
      now.get(java.util.Calendar.YEAR)
    );      
  }  
  
  
  public static PointInTime getPointInTime(String string) {
    PointInTime result = new PointInTime(UNKNOWN,UNKNOWN,UNKNOWN,GREGORIAN);
    result.set(string);
    return result;
  }
  
  
  public static PointInTime getPointInTime(long millis) {
    
    long julian = 2440588 + (millis / 24 / 60 / 60 / 1000);
    return GREGORIAN.toPointInTime((int)julian);
  }
  
  
  public long getTimeMillis() throws GedcomException {
    
    return (getJulianDay()-2440588L) * 24*60*60*1000;
  }

  
  public PointInTime getPointInTime(Calendar cal) throws GedcomException {
    if (calendar==cal)
      return this;
    PointInTime result = new PointInTime();
    result.set(this);
    result.set(cal);
    return result;
  }
  
  
  public int getJulianDay() throws GedcomException {
    if (jd==UNKNOWN)
      jd = calendar.toJulianDay(this);
    return jd;
  }
  
  
  public void reset() {
    set(UNKNOWN,UNKNOWN,UNKNOWN);
  }
  
  
  public String getDayOfWeek(boolean localize) throws GedcomException {
    return calendar.getDayOfWeek(this, localize);
  }
  
  
  public void set(Calendar cal) throws GedcomException {
    
    if (day==UNKNOWN&&month==UNKNOWN&&year==UNKNOWN) {
      calendar = cal;
      return;
    }
    
    if (!isValid())
      throw new GedcomException(resources.getString("pit.invalid"));
    
    if (!isComplete())
      throw new GedcomException(resources.getString("pit.incomplete"));
    
    int jd = getJulianDay();
    
    set(cal.toPointInTime(jd));
  }  
  
  
  public void set(int d, int m, int y) {
    set(d,m,y,calendar);
  }
  
  
  public void set(int d, int m, int y, Calendar cal) {
    day = d;
    month = m;
    year = y;
    calendar = cal;
    jd = UNKNOWN;
  }
  
  
  public void set(PointInTime other) {
    calendar = other.calendar;
    set(other.getDay(), other.getMonth(), other.getYear());
  }
  
  
  public boolean set(String txt) {

    txt = txt.trim();
    
    
    calendar = GREGORIAN;
    
    
    if (txt.startsWith("@#")) {
      int i = txt.indexOf("@", 1);
      if (i<0)  return false;
      String esc = txt.substring(0,i+1);
      txt = txt.substring(i+1);
      
      
      for (int c=0;c<CALENDARS.length;c++) {
        Calendar cal = CALENDARS[c]; 
        if (cal.escape.equalsIgnoreCase(esc)) {
        
          calendar = cal;
          break;
        }
      }
    }
    
    
    DirectAccessTokenizer tokens = new DirectAccessTokenizer(txt, " ", true);
    String first = tokens.get(0);
    if (first==null) {
      reset();
      return true;
    }
    int cont = 1;

    
    String second = tokens.get(cont++);
        
    
    if (second==null) {
        try {
          set(UNKNOWN, UNKNOWN, calendar.getYear(first));
        } catch (Throwable t) {
          return false;
        }
        return getYear()!=UNKNOWN;
    }
    
    
    String third = tokens.get(cont++);
    
    
    if (third==null) {
      try {
        if (calendar==FRENCHR) set(UNKNOWN, UNKNOWN, calendar.getYear(first + ' ' + second));
      } catch (Throwable t) {
      }
      try {
        set(UNKNOWN, calendar.parseMonth(first),  calendar.getYear(second));
      } catch (Throwable t) {
        return false;
      }
      return getYear()!=UNKNOWN&&getMonth()!=UNKNOWN;
    }

    
    third = txt.substring(tokens.getStart());
    
    try {
      set( Integer.parseInt(first) - 1, calendar.parseMonth(second), calendar.getYear(third));
    } catch (Throwable t) {
      return false;
    }
    return getYear()!=UNKNOWN&&getMonth()!=UNKNOWN&&getDay()!=UNKNOWN;
    
  }
  
  
  public boolean isGregorian() {
    return getCalendar()==GREGORIAN;    
  }

  
  public boolean isComplete() {
    return year!=UNKNOWN && month!=UNKNOWN && day!=UNKNOWN;
  }

  
  public boolean isValid() {
    
    
    if (jd!=UNKNOWN)
      return true;

    
    
    if (day==UNKNOWN&&month==UNKNOWN&&year==UNKNOWN)
      return false;

    
    try {
      jd = calendar.toJulianDay(this);
    } catch (GedcomException e) {
    }
    
    
    return jd!=UNKNOWN;
  }
    
    
  public int compareTo(Object o) {
    return compareTo((PointInTime)o);
  }    

    
  public int compareTo(PointInTime other) {
    
    
    boolean
      v1 = isValid(),
      v2 = other.isValid();
    if (!v1&&!v2)
      return 0;
    if (!v2)
      return 1;
    if (!v1)
      return -1; 
    
    try {
      return getJulianDay() - other.getJulianDay();
    } catch (GedcomException e) {
      return 0; 
    }
  }

  
  public String getValue() {
    return getValue(new WordBuffer()).toString();
  }
    
  
  public WordBuffer getValue(WordBuffer buffer) {
    if (calendar!=GREGORIAN)
      buffer.append(calendar.escape);
    toString(buffer, FORMAT_GEDCOM);
    return buffer;
  }
    
  
  public String toString() {
    return toString(new WordBuffer()).toString();
  }

  
  public WordBuffer toString(WordBuffer buffer) {
    return toString(buffer, Options.getInstance().dateFormat);
  }
  
  
  private static DateFormat NUMERICDATEFORMAT = initNumericDateFormat();
  
  private static DateFormat initNumericDateFormat() {
    DateFormat result = DateFormat.getDateInstance(DateFormat.SHORT);
    try {
      
      String pattern = ((SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT)).toPattern();
      
      int yyyy = pattern.indexOf("yyyy");
      if (yyyy<0) 
        result = new SimpleDateFormat(pattern.replaceAll("yy", "yyyy"));
    } catch (Throwable t) {
    }
    return result;
  }
  
  
  public static void localeChangedNotify() {
    NUMERICDATEFORMAT = initNumericDateFormat();
  }
  
  
  public WordBuffer toString(WordBuffer buffer, int format) {
    
    
    if (format==FORMAT_NUMERIC) {
      if (calendar==GREGORIAN&&isComplete()) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(year, month, day+1);
        buffer.append(NUMERICDATEFORMAT.format(c.getTime()));
        return buffer;
      }
      
      
      format = FORMAT_SHORT;
    }
    
    
    if (!isValid()) {
      
      if (format!=FORMAT_GEDCOM)
        buffer.append("?");
      return buffer;
    }
        
    
    if (year!=UNKNOWN) {
      if (month!=UNKNOWN) {
        if (day!=UNKNOWN) {
          buffer.append(new Integer(day+1));
        }
        buffer.append(format==FORMAT_GEDCOM ? calendar.getMonth(month) : calendar.getDisplayMonth(month, format==FORMAT_SHORT));
      }
      buffer.append(format==FORMAT_GEDCOM ? calendar.getYear(year) : calendar.getDisplayYear(year));
      
      
      if (format!=FORMAT_GEDCOM&&calendar==JULIAN)
        buffer.append("(j)");
    }      
    
    
    return buffer;
  }

} 