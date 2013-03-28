
package genj.gedcom.time;




public class GregorianCalendar extends Calendar {

  protected static final String MONTHS[]
    = { "JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC" };
    
  protected static final String WEEKDAYS[]
    = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

  private static final int MONTH_LENGTH[]
    = {31,28,31,30,31,30,31,31,30,31,30,31}; 
    
  private static final int LEAP_MONTH_LENGTH[]
    = {31,29,31,30,31,30,31,31,30,31,30,31}; 

  
  protected GregorianCalendar() {
    this("@#DGREGORIAN@", "gregorian", "images/Gregorian");
  }
  
  
  protected GregorianCalendar(String esc, String key, String img) {
    super(esc, key, img, MONTHS, WEEKDAYS);
  }

  
  public int getDays(int month, int year) {
    int[] length = isLeap(year) ? GregorianCalendar.LEAP_MONTH_LENGTH : GregorianCalendar.MONTH_LENGTH;
    return length[month];
  }
  
  
  protected boolean isLeap(int year) {
    return ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0)));
  }
  
  
  protected int toJulianDay(int day, int month, int year) {

    
    if (year<0)
      year++;

    
    
    
    int
     d = day   + 1,
     m = month + 1,
     y = year     ;      
    
    return ( 1461 * ( y + 4800 + ( m - 14 ) / 12 ) ) / 4 +
           ( 367 * ( m - 2 - 12 * ( ( m - 14 ) / 12 ) ) ) / 12 -
           ( 3 * ( ( y + 4900 + ( m - 14 ) / 12 ) / 100 ) ) / 4 +
           d - 32075;
  }
  
  
  protected PointInTime toPointInTime(int julianDay) {
   
    
    int l = julianDay + 68569;
    int n = ( 4 * l ) / 146097;
        l = l - ( 146097 * n + 3 ) / 4;
    int i = ( 4000 * ( l + 1 ) ) / 1461001;
        l = l - ( 1461 * i ) / 4 + 31;
    int j = ( 80 * l ) / 2447;
    int d = l - ( 2447 * j ) / 80;
        l = j / 11;
    int m = j + 2 - ( 12 * l );
    int y = 100 * ( n - 49 ) + i + l;
    
    return new PointInTime(d-1,m-1,y<=0?y-1:y,this);
  }
  
} 