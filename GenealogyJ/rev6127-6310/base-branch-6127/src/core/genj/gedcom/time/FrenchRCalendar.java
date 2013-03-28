
package genj.gedcom.time;

import genj.gedcom.GedcomException;



public class FrenchRCalendar extends Calendar {
  
  
  private static final int
    AN_0  = 2375474,
    AN_I  = new GregorianCalendar().toJulianDay(22-1, 9-1, 1792),
    UNTIL = new GregorianCalendar().toJulianDay( 1-1, 1-1, 1806);

  private static final String MONTHS[] 
   = { "VEND","BRUM","FRIM","NIVO","PLUV","VENT","GERM","FLOR","PRAI","MESS","THER","FRUC","COMP" };
  
  private static final String WEEKDAYS[] 
   = { "PRI", "DUO", "TRI", "QUA", "QUI", "SEX", "SEP", "OCT", "NON", "DEC", "VER", "GEN", "TRA", "OPI", "REC", "REV" };
   
  private static final int[] LEAP_YEARS
   = { 3,7,11 };
   
  private static final String YEARS_PREFIX = "An ";
   
  private static final String[] YEARS 
   = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV" };

  private static final int
    DAYS_PER_MONTH   = 30,
    DAYS_PER_4_YEARS = 1461;
  
  
  protected FrenchRCalendar() {
    super("@#DFRENCH R@" , "french", "images/FrenchR", MONTHS, WEEKDAYS);
  }
  
  
  public int getDays(int month, int year) {
    
    
    if (month<12)
      return 30;
      
    
    return isLeap(year) ? 6 : 5;
    
    
  }
  
  
  private boolean isLeap(int year) {
    return (year+1) % 4 == 0;
  }
  
  
  protected String getDayOfWeek(PointInTime pit, boolean localize) throws GedcomException {
    if (!pit.isComplete()) 
      throw new GedcomException("");
    
    String[] result = localize ? localizedWeekDays : weekDays;
    
    if (pit.getMonth()==13-1)
      return result[10+pit.getDay()];
    
    return result[pit.getDay()%10];
  }

  
  protected int toJulianDay(int day, int month, int year) throws GedcomException {
    
    int jd = ( year * DAYS_PER_4_YEARS / 4
      + month * DAYS_PER_MONTH
      + day+1
      + AN_0 );
    
    if (jd<FrenchRCalendar.AN_I)
      throw new GedcomException(resources.getString("frenchr.bef"));
    if (jd>=FrenchRCalendar.UNTIL)
      throw new GedcomException(resources.getString("frenchr.aft"));
    
    return jd;

  }
  
  
  protected PointInTime toPointInTime(int julianDay) throws GedcomException {

    
    if (julianDay<FrenchRCalendar.AN_I)
      throw new GedcomException(resources.getString("frenchr.bef"));
    if (julianDay>=FrenchRCalendar.UNTIL)
      throw new GedcomException(resources.getString("frenchr.aft"));
    
    int temp = (julianDay - AN_0) * 4 - 1;
    
    int year = temp / DAYS_PER_4_YEARS;
    int dayOfYear = (temp % DAYS_PER_4_YEARS) / 4;
    int month = dayOfYear / DAYS_PER_MONTH + 1;
    int day = dayOfYear % DAYS_PER_MONTH + 1;
          
    
    return new PointInTime(day-1,month-1,year,this);
  }
  
  
  public String getDisplayYear(int year) {
    if (year<1||year>FrenchRCalendar.YEARS.length)
      return super.getDisplayYear(year);
    return YEARS_PREFIX+FrenchRCalendar.YEARS[year-1];
  }

  
  public int getYear(String year) throws GedcomException {
    
    if (year.length()>YEARS_PREFIX.length() && year.substring(0, YEARS_PREFIX.length()).equalsIgnoreCase(YEARS_PREFIX))
      year = year.substring(YEARS_PREFIX.length());
    
    for (int y=0;y<YEARS.length;y++)
      if (YEARS[y].equals(year))
        return y+1;
    
    return super.getYear(year);
  }

} 