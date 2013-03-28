
package genj.gedcom;

import java.util.Locale;

import genj.gedcom.PropertyDate.Format;
import genj.gedcom.time.Calendar;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import junit.framework.TestCase;


public class PropertyDateTest extends TestCase {
  
  private PropertyDate date = new PropertyDate();
  private PointInTime pit = new PointInTime();

  private Calendar
    GREGORIAN = PointInTime.GREGORIAN,
    JULIAN = PointInTime.JULIAN,
    HEBREW = PointInTime.HEBREW,
    FRENCHR = PointInTime.FRENCHR;
  
  private int
    FORMAT_GEDCOM = PointInTime.FORMAT_GEDCOM,
    FORMAT_SHORT = PointInTime.FORMAT_SHORT,
    FORMAT_LONG = PointInTime.FORMAT_LONG,
    FORMAT_NUMERIC = PointInTime.FORMAT_NUMERIC;
   
  
  
  public void testFormatting() {     
    
    testFormat("25 JAN 1970", FORMAT_GEDCOM, "25 JAN 1970");
    testFormat("25 JAN 1970", FORMAT_SHORT, "25 Jan 1970");
    testFormat("25 JAN 1970", FORMAT_LONG, "25 "+PointInTime.GREGORIAN.getDisplayMonth(0, false)+" 1970");
    
    Locale.setDefault(Locale.ENGLISH);
    PointInTime.localeChangedNotify();
    testFormat("25 JAN 1970", FORMAT_NUMERIC, "1/25/1970");
    testFormat("JAN 1970", FORMAT_NUMERIC, "Jan 1970");
    testFormat("1970", FORMAT_NUMERIC, "1970");
    
    Locale.setDefault(Locale.GERMAN);
    PointInTime.localeChangedNotify();
    testFormat("25 JAN 1970", FORMAT_NUMERIC, "25.01.1970");
    testFormat("JAN 1970", FORMAT_NUMERIC, "Jan 1970");
    testFormat("1970", FORMAT_NUMERIC, "1970");
    
  }
  
  private void testFormat(String value, int format, String display) {
    Options.getInstance().dateFormat = format;
    pit.set(value);
    assertEquals(display, pit.toString());
  }
  
  
  
  public void testDates() {     

    testParsing("", PropertyDate.DATE, GREGORIAN, 0, 0, 0, true);
    assertFalse(date.isValid());
    date.setValue(PropertyDate.DATE, PointInTime.getNow(), null, null);
    assertTrue(date.isValid()); 
    
    testParsing("25 MAY 1970", PropertyDate.DATE, GREGORIAN, 1970, 5, 25, true);
    
    testParsing("@#DJULIAN@ 25 MAY 1970", PropertyDate.DATE, JULIAN, 1970, 5, 25, true);
    testParsing("@#DFRENCH R@ 3 GERM An I", PropertyDate.DATE, FRENCHR, 1, 7, 3, false);
    testParsing("@#DHEBREW@ 1     CSH 5000", PropertyDate.DATE, HEBREW, 5000,  2, 1, false);

    testParsing("FROM 1 AUG 1999 TO 1 SEP 2001", PropertyDate.FROM_TO, GREGORIAN, 1999, 8, 1, GREGORIAN, 2001, 9, 1, true);
    testParsing("FROM @#DFRENCH R@ 3 GERM 1", PropertyDate.FROM, FRENCHR, 1, 7, 3, true);
    testParsing("TO @#DFRENCH R@ 3 GERM An I", PropertyDate.TO, FRENCHR, 1, 7, 3, false);
    testParsing("BET   @#DHEBREW@ 1    CSH 5000   AND @#DFRENCH R@ 3 GERM 1", PropertyDate.BETWEEN_AND, HEBREW, 5000,  2, 1, FRENCHR, 1, 7, 3,false);
    testParsing("BEF 25 MAY 1970", PropertyDate.BEFORE, GREGORIAN, 1970, 5, 25, true);
    testParsing("AFT     @#DHEBREW@ 1     CSH 5000  ", PropertyDate.AFTER, HEBREW, 5000,  2, 1, false);

    testParsing("ABT 1970", PropertyDate.ABOUT, GREGORIAN, 1970, 0, 0, true);
    testParsing("CAL MAY 1970", PropertyDate.CALCULATED, GREGORIAN, 1970, 5, 0, true);
    testParsing("EST @#DJULIAN@ 25 May 1970", PropertyDate.ESTIMATED, JULIAN, 1970, 5, 25, false);
    
    testParsing("Bef Sep 1846", PropertyDate.BEFORE, GREGORIAN, 1846, 9, 0, false);
    
    testParsing("INT 25 MAY 1970 (foo)", PropertyDate.INTERPRETED, GREGORIAN, 1970, 5, 25, true);
    testParsing("(sometime in may)", PropertyDate.INTERPRETED, GREGORIAN, 0, 0, 0, true);
    testParsing("foo bar", PropertyDate.DATE, GREGORIAN, 0, 0, 0, false);
    assertFalse(date.isValid());
    
    
    
    
    testParsing("@#DJulian@25 May 1970", PropertyDate.DATE, JULIAN, 1970, 5, 25, false);
    
    
  }
  
  private void testParsing(String value, Format format, Calendar cal, int year, int month, int day, boolean verbatim) {
    date.setValue(value);
    if (verbatim)
      assertEquals(value, date.getValue());
    assertEquals("wrong format", format, date.getFormat());
    testPIT(date.getStart(), cal, year, month, day);
  }
  
  private void testParsing(String value, Format format, Calendar cal1, int year1, int month1, int day1, Calendar cal2, int year2, int month2, int day2, boolean verbatim) {
    date.setValue(value);
    if (verbatim)
      assertEquals(value, date.getValue());
    assertEquals("wrong format", date.getFormat(), format);
    testPIT(date.getStart(), cal1, year1, month1, day1);
    testPIT(date.getEnd(), cal2, year2, month2, day2);
  }
  
  private void testPIT(PointInTime pit, Calendar cal, int year, int month, int day) {
    
    assertEquals("calendar of "+pit, cal, pit.getCalendar());
    
    if (year>0) 
      assertEquals("year of "+pit, year, pit.getYear());
    else
      assertTrue("year of "+pit+" should be unknown", pit.getYear()==PointInTime.UNKNOWN);
    
    if (month>0) 
      assertEquals("month of "+pit, month-1, pit.getMonth());
    else
      assertTrue("month of "+pit+" should be unknown", pit.getMonth()==PointInTime.UNKNOWN);
    
    if (day>0) 
      assertEquals("day of "+pit, day-1, pit.getDay());
    else
      assertTrue("day of "+pit+" should be unknown", pit.getDay()==PointInTime.UNKNOWN);
    
    
  }
  
  private final static int X = PointInTime.UNKNOWN;

  public void testAnniversaries() {
    
    PointInTime now = new PointInTime(25-1, 5-1, 1970);
    
    testAnniversary(X, X, 1970, now, 0, 0, 0);
    testAnniversary(X, 5, 1970, now, 0, 0, 0);
    testAnniversary(X, 4, 1970, now, 0, 1, 0);
    testAnniversary(24, 5, 1970, now, 1, 0, 0);
    testAnniversary(24, 4, 1970, now, 1, 1, 0);
    testAnniversary(24, 4, 1969, now, 1, 1, 1);
    
    testAnniversary(1, 1, 1900, now, 24, 4, 70);
  }
  
  private void testAnniversary(int day, int month, int year, PointInTime now, int days, int months, int years) {
    
    PropertyDate pd = new PropertyDate();
    pd.setValue(PropertyDate.DATE, new PointInTime( day==X?X:day-1, month==X?X:month-1, year), null, null);
    
    Delta anniversary = pd.getAnniversary(now);
    
    assertEquals("anniversary of "+pd+" (days)", days, anniversary.getDays());
    assertEquals("anniversary of "+pd+" (months)", months, anniversary.getMonths());
    assertEquals("anniversary of "+pd+" (years)", years, anniversary.getYears());
    
    
  }
  
} 
