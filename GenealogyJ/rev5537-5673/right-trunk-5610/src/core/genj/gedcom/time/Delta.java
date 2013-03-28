
package genj.gedcom.time;

import genj.util.WordBuffer;

import java.util.StringTokenizer;


public class Delta implements Comparable {

  
  public final static String
    TXT_DAY     = PointInTime.resources.getString("time.day"   ),
    TXT_DAYS    = PointInTime.resources.getString("time.days"  ),
    TXT_DAYSS,
    TXT_MONTH   = PointInTime.resources.getString("time.month" ),
    TXT_MONTHS  = PointInTime.resources.getString("time.months"),
    TXT_MONTHSS,
    TXT_YEAR    = PointInTime.resources.getString("time.year"  ),
    TXT_YEARS   = PointInTime.resources.getString("time.years" ),
    TXT_YEARSS;

  
  static {
      
      String ss = PointInTime.resources.getString("time.dayss");
      if (ss.equals("time.dayss"))
          TXT_DAYSS = PointInTime.resources.getString("time.days");
      else
          TXT_DAYSS = ss;

      
      ss = PointInTime.resources.getString("time.monthss");
      if (ss.equals("time.monthss"))
          TXT_MONTHSS = PointInTime.resources.getString("time.months");
      else
          TXT_MONTHSS = ss;

      
      ss = PointInTime.resources.getString("time.yearss");
      if (ss.equals("time.yearss"))
          TXT_YEARSS = PointInTime.resources.getString("time.years");
      else
          TXT_YEARSS = ss;
  }

  
  private int years, months, days;
  private Calendar calendar;

  
  public Delta(int d, int m, int y) {
    this(d,m,y,PointInTime.GREGORIAN);
  }

  
  public Delta(int d, int m, int y, Calendar c) {
    years = y;
    months= m;
    days  = d;
    calendar = c;
  }

  
  public int getYears() {
    return years;
  }

  
  public int getMonths() {
    return months;
  }

  
  public int getDays() {
    return days;
  }

  
  public Calendar getCalendar() {
    return calendar;
  }

  
  public static Delta get(PointInTime earlier, PointInTime later, Calendar calendar) {

    
    try {
      earlier = earlier.getPointInTime(calendar);
      later = later.getPointInTime(calendar);
    } catch (Throwable t) {
      return null;
    }

    
    if (earlier.compareTo(later)>0) {
      PointInTime p = earlier;
      earlier = later;
      later = p;
    }
    
    
    int
      yearlier =  earlier.getYear (),
      mearlier = earlier.getMonth(),
      dearlier = earlier.getDay();

    
    int
      ylater =  later.getYear (),
      mlater = later.getMonth(),
      dlater = later.getDay();

    
    if (yearlier==PointInTime.UNKNOWN||ylater==PointInTime.UNKNOWN)
      return null;
    int years  = ylater - yearlier;

    
    int months = 0;
    int days = 0;
    if (mearlier!=PointInTime.UNKNOWN&&mlater!=PointInTime.UNKNOWN) {

      
      months = mlater - mearlier;

      
      if (dearlier!=PointInTime.UNKNOWN&&dlater!=PointInTime.UNKNOWN) {

        
        days = dlater - dearlier;

        
        if (days<0) {
          
          months --;
          
          days = dlater + (calendar.getDays(mearlier, yearlier)-dearlier);
        }

      }

      
      if (months<0) {
        
        years -=1;
        
        months += calendar.getMonths();
      }

    }
    
    return new Delta(days, months, years, calendar);
  }
  
  
  
  public static Delta get(PointInTime earlier, PointInTime later) {
    return get(earlier, later, PointInTime.GREGORIAN);
  }

  
  public int compareTo(Object o) {
    Delta other = (Delta)o;
    
    int delta = years - other.years;
    if (delta != 0)
      return delta;
    
    delta = months - other.months;
    if (delta != 0)
      return delta;
    
    delta = days - other.days;
    return delta;
  }


  
  public String toString() {
    
    if (years==0&&months==0&&days==0) {
      return "<1 "+TXT_DAY;
    }

    WordBuffer buffer = new WordBuffer();
    if (years >0) {
      buffer.append(years);
      int form = getPluralForm(years);
      if (form == 1)
          buffer.append(TXT_YEAR);
      else if (form == 2)
          buffer.append(TXT_YEARS);
      else
          buffer.append(TXT_YEARSS);
    }
    if (months>0) {
      buffer.append(months);
      int form = getPluralForm(months);
      if (form == 1)
          buffer.append(TXT_MONTH);
      else if (form == 2)
          buffer.append(TXT_MONTHS);
      else
          buffer.append(TXT_MONTHSS);
    }
    if (days  >0) {
      buffer.append(days);
      int form = getPluralForm(days);
      if (form == 1)
          buffer.append(TXT_DAY);
      else if (form == 2)
          buffer.append(TXT_DAYS);
      else
          buffer.append(TXT_DAYSS);
    }
    return buffer.toString();
  }

  
  private int getPluralForm(int number) {
      number = Math.abs(number);
      if (number == 1)
          return 1;
      if (number % 100 / 10 != 1 && number % 10 >= 2 && number % 10 <= 4)
          return 2;
      return 3;
  }

  
  public String getValue() {
    WordBuffer buffer = new WordBuffer();
    if (years >0) buffer.append(years+"y");
    if (months>0) buffer.append(months+"m");
    if ( (years==0&&months==0) || (years>0&&months>0) || days>0) buffer.append(days +"d");
    return buffer.toString();
  }

  
  public boolean setValue(String value) {

    
    years = 0;
    months = 0;
    days = 0;

    
    StringTokenizer tokens = new StringTokenizer(value);
    while (tokens.hasMoreTokens()) {

        String token = tokens.nextToken();
        int len = token.length();

        
        if (len<2) return false;
        for (int i=0;i<len-1;i++) {
            if (!Character.isDigit(token.charAt(i)))
              return false;
        }

        int i;
        try {
          i = Integer.parseInt(token.substring(0, token.length()-1));;
        } catch (NumberFormatException e) {
          return false;
        }

        
        switch (token.charAt(len-1)) {
            case 'y' : years = i; break;
            case 'm' : months= i; break;
            case 'd' : days  = i; break;
            default  : return false;
        }
    }

    
    return true;
  }

} 