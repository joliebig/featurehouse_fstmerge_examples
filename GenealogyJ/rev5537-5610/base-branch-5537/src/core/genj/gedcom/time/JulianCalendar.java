
package genj.gedcom.time;




public class JulianCalendar extends GregorianCalendar {

  
  protected JulianCalendar() {
    super("@#DJULIAN@", "julian", "images/Julian");
  }
  
  
  protected boolean isLeap(int year) {
    return (year%4 == 0);
  }
  
  
  protected int toJulianDay(int day, int month, int year) {

    
    if (year<0)
      year++;

    int 
      y = year,
      m = month+1,
      d = day+1;
    
    if (m<2) {
      y--;
      m+=12;
    }
          
    int
      E = (int)(365.25*(y+4716)),
      F = (int)(30.6001*(m+1)),
      JD= d+E+F-1524;
          
    return JD;
  }
  
  
  protected PointInTime toPointInTime(int julianDay) {

    
    
    int
      Z = julianDay,
      B = Z+1524,
      C = (int)((B-122.1)/365.25),
      D = (int)(365.25*C),
      E = (int)((B-D)/30.6001),
      F = (int)(30.6001*E),
      d = B-D-F,
      m = E-1 <= 12 ? E-1 : E-13,
      y = C-(m<3?4715:4716);  
    
    return new PointInTime(d-1,m-1,y<=0?y-1:y,this);
  }

} 