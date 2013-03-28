
package genj.gedcom.time;

import genj.gedcom.GedcomException;


public class HebrewCalendar extends Calendar {

  private static final String[] MONTHS 
   = { "TSH","CSH","KSL","TVT","SHV","ADR","ADS","NSN","IYR","SVN","TMZ","AAV","ELL" };

  private static final String[] WEEKDAYS 
  = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAB" };
   
  
  protected HebrewCalendar() {
    super("@#DHEBREW@", "hebrew", "images/Hebrew", MONTHS, WEEKDAYS);
  }
  
  
  protected PointInTime toPointInTime(int julianDay) throws GedcomException {
    
    
    if (julianDay<=SDN_OFFSET)
      throw new GedcomException(resources.getString("hebrew.bef"));

    
    return SdnToJewish(julianDay);
  }
  
  
  protected int toJulianDay(int day, int month, int year) throws GedcomException {

    
    if (year<1)
      throw new GedcomException(resources.getString("hebrew.one"));

    
    return JewishToSdn(year, month+1, day+1);
  }
  
  
  public int getDays(int month, int year) {
    
    
    switch (month) {
      case  3: 
      case  6: 
      case  8: 
      case 10: 
      case 12: 
        return 29;
      case  1: 
        if (getDays(year)%10!=5)
          return 29;
        break; 
      case  2: 
        if (getDays(year)%10==3)
          return 29;
        break;
    }   

    
    return 30;
  }

    
  private int getDays(int year) {
    try {
      return toJulianDay(1,1,year+1) - toJulianDay(1,1,year); 
    } catch (Throwable t) {
      
      throw new RuntimeException();
    }
  }

  
  
  

  private class Molad {
    int day;
    int halakim;
  }

  private class Metonic {
    int cycle;
    int year;
  }
  
  private PointInTime wrap(int day, int month, int year) {
    return new PointInTime(day-1, month-1, year, this);
  }
  
  
  
  
  
  private final static int
   HALAKIM_PER_HOUR = 1080,
   HALAKIM_PER_DAY = 25920,
   HALAKIM_PER_LUNAR_CYCLE = ((29 * HALAKIM_PER_DAY) + 13753),
   HALAKIM_PER_METONIC_CYCLE = (HALAKIM_PER_LUNAR_CYCLE * (12 * 19 + 7)),

   SDN_OFFSET = 347997,
   NEW_MOON_OF_CREATION = 31524,

   SUNDAY    = 0,
   MONDAY    = 1,
   TUESDAY   = 2,
   WEDNESDAY = 3,
   THURSDAY  = 4,
   FRIDAY    = 5,
   SATURDAY  = 6,

   NOON      = (18 * HALAKIM_PER_HOUR),
   AM3_11_20 = ((9 * HALAKIM_PER_HOUR) + 204),
   AM9_32_43 = ((15 * HALAKIM_PER_HOUR) + 589);
  
  private static int[] monthsPerYear = {
      12, 12, 13, 12, 12, 13, 12, 13, 12, 12, 13, 12, 12, 13, 12, 12, 13, 12, 13
  };

  private static int[] yearOffset = {
      0, 12, 24, 37, 49, 61, 74, 86, 99, 111, 123,
      136, 148, 160, 173, 185, 197, 210, 222
  };
  
  
  private int getTishri1(int metonicYear, Molad molad) {
    
    int tishri1;
    int dow;
    boolean leapYear;
    boolean lastWasLeapYear;

    tishri1 = molad.day;
    dow = tishri1 % 7;
    
    leapYear = metonicYear == 2 || metonicYear == 5 || metonicYear == 7
      || metonicYear == 10 || metonicYear == 13 || metonicYear == 16
      || metonicYear == 18;
    lastWasLeapYear = metonicYear == 3 || metonicYear == 6
      || metonicYear == 8 || metonicYear == 11 || metonicYear == 14
      || metonicYear == 17 || metonicYear == 0;

    
    if ((molad.halakim >= NOON) ||
      ((!leapYear) && dow == TUESDAY && molad.halakim >= AM3_11_20) ||
      (lastWasLeapYear && dow == MONDAY && molad.halakim >= AM9_32_43)) {
      tishri1++;
      dow++;
      if (dow == 7) {
          dow = 0;
      }
    }

    
    if (dow == WEDNESDAY || dow == FRIDAY || dow == SUNDAY) {
      tishri1++;
    }

    return(tishri1);
  }
  
  
  private void getMoladOfMetonicCycle(int metonicCycle, Molad molad) {
    
      int r1, r2, d1, d2;

      
      r1 = NEW_MOON_OF_CREATION;

      
      r1 += metonicCycle * (HALAKIM_PER_METONIC_CYCLE & 0xFFFF);
      r2 = r1 >> 16;
      r2 += metonicCycle * ((HALAKIM_PER_METONIC_CYCLE >> 16) & 0xFFFF);

      
      d2 = r2 / HALAKIM_PER_DAY;
      r2 -= d2 * HALAKIM_PER_DAY;
      r1 = (r2 << 16) | (r1 & 0xFFFF);
      d1 = r1 / HALAKIM_PER_DAY;
      r1 -= d1 * HALAKIM_PER_DAY;

      molad.day  = (d2 << 16) | d1;
      molad.halakim = r1;
      
  }

  
  private void FindTishriMolad(int inputDay, Metonic metonic, Molad molad) { 

    int metonicCycle;
    int metonicYear;

    
    metonicCycle = (inputDay + 310) / 6940;

    
    getMoladOfMetonicCycle(metonicCycle, molad);

    
    while (molad.day < inputDay - 6940 + 310) {
      metonicCycle++;
      molad.halakim += HALAKIM_PER_METONIC_CYCLE;
      molad.day += molad.halakim / HALAKIM_PER_DAY;
      molad.halakim = molad.halakim % HALAKIM_PER_DAY;
    }

    
    for (metonicYear = 0; metonicYear < 18; metonicYear++) {
      if (molad.day > inputDay - 74)
        break;
      molad.halakim += HALAKIM_PER_LUNAR_CYCLE * monthsPerYear[metonicYear];
      molad.day += molad.halakim / HALAKIM_PER_DAY;
      molad.halakim = molad.halakim % HALAKIM_PER_DAY;
    }

    metonic.cycle = metonicCycle;
    metonic.year = metonicYear;
      
  }

  
  private int FindStartOfYear(int year, Metonic metonic, Molad molad) {
    
    metonic.cycle = (year - 1) / 19;
    metonic.year = (year - 1) % 19;
    
    getMoladOfMetonicCycle(metonic.cycle, molad);

    molad.halakim += HALAKIM_PER_LUNAR_CYCLE * yearOffset[metonic.year];
    molad.day += molad.halakim / HALAKIM_PER_DAY;
    molad.halakim = molad.halakim % HALAKIM_PER_DAY;

    return getTishri1(metonic.year, molad);
  }

  
  private PointInTime SdnToJewish(int sdn) {

    int year, month, day;
    
    Molad molad = new Molad();
    Metonic metonic = new Metonic();
    int inputDay;
    int tishri1;
    int tishri1After;
    int yearLength;

    if (sdn <= SDN_OFFSET)
      return null;
      
    inputDay = sdn - SDN_OFFSET;

    FindTishriMolad(inputDay, metonic, molad);
    
    tishri1 = getTishri1(metonic.year, molad);

    if (inputDay >= tishri1) {
      
      year = metonic.cycle * 19 + metonic.year + 1;
      if (inputDay < tishri1 + 59) {
        if (inputDay < tishri1 + 30) {
          month = 1;
          day = inputDay - tishri1 + 1;
        } else {
          month = 2;
          day = inputDay - tishri1 - 29;
        }
        return wrap(day, month, year);
      }

      
      molad.halakim += HALAKIM_PER_LUNAR_CYCLE * monthsPerYear[metonic.year];
      molad.day += molad.halakim / HALAKIM_PER_DAY;
      molad.halakim = molad.halakim % HALAKIM_PER_DAY;
      tishri1After = getTishri1((metonic.year + 1) % 19, molad);
      
    } else {
      
      
      year = metonic.cycle * 19 + metonic.year;
      if (inputDay >= tishri1 - 177) {
        
        
        if (inputDay > tishri1 - 30) {
          month = 13;
          day = inputDay - tishri1 + 30;
        } else if (inputDay > tishri1 - 60) {
          month = 12;
          day = inputDay - tishri1 + 60;
        } else if (inputDay > tishri1 - 89) {
          month = 11;
          day = inputDay - tishri1 + 89;
        } else if (inputDay > tishri1 - 119) {
          month = 10;
          day = inputDay - tishri1 + 119;
        } else if (inputDay > tishri1 - 148) {
          month = 9;
          day = inputDay - tishri1 + 148;
        } else {
          month = 8;
          day = inputDay - tishri1 + 178;
        }
        
        return wrap(day, month, year);
        
      } else {
        
        if (monthsPerYear[(year - 1) % 19] == 13) {
          month = 7;
          day = inputDay - tishri1 + 207;
          if (day > 0) 
            return wrap(day, month, year);
          (month)--;
          (day) += 30;
          if (day > 0) 
            return wrap(day, month, year);
          (month)--;
          (day) += 30;
        } else {
          month = 6;
          day = inputDay - tishri1 + 207;
          if (day > 0) 
            return wrap(day, month, year);
          (month)--;
          (day) += 30;
        }
        if (day > 0) 
          return wrap(day, month, year);
        (month)--;
        (day) += 29;
        if (day > 0) 
          return wrap(day, month, year);

        
        tishri1After = tishri1;
        FindTishriMolad(molad.day - 365, metonic, molad);
        tishri1 = getTishri1(metonic.year, molad);
      }
    }

    yearLength = tishri1After - tishri1;
    day = inputDay - tishri1 - 29;

    if (yearLength == 355 || yearLength == 385) {
      
      if (day <= 30) {
        month = 2;
        return wrap(day, month, year);
      }
      day -= 30;
    } else {
      
      if (day <= 29) {
        month = 2;
        return wrap(day, month, year);
      }
      day -= 29;
    }

    
    month = 3;
    
    return wrap(day, month, year);
  }

  
  private int JewishToSdn(int year, int month, int day) {
    
    int sdn;
    int tishri1;
    int tishri1After;
    int yearLength;
    int lengthOfAdarIAndII;
    Molad molad = new Molad();
    Metonic metonic = new Metonic();

    if (year <= 0 || day <= 0 || day > 30)
      return(0);

    switch (month) {
      case 1:
      case 2:
        
        tishri1 = FindStartOfYear(year, metonic, molad);
        if (month == 1) {
            sdn = tishri1 + day - 1;
        } else {
            sdn = tishri1 + day + 29;
        }
        break;
      case 3:
        

        
        tishri1 = FindStartOfYear(year, metonic, molad);

        
        molad.halakim += HALAKIM_PER_LUNAR_CYCLE * monthsPerYear[metonic.year];
        molad.day += molad.halakim / HALAKIM_PER_DAY;
        molad.halakim = molad.halakim % HALAKIM_PER_DAY;
        
        tishri1After = getTishri1((metonic.year + 1) % 19, molad);

        yearLength = tishri1After - tishri1;

        if (yearLength == 355 || yearLength == 385) {
            sdn = tishri1 + day + 59;
        } else {
            sdn = tishri1 + day + 58;
        }
        break;
      case 4:
      case 5:
      case 6:
        
        tishri1After = FindStartOfYear(year + 1, metonic, molad);

        if (monthsPerYear[(year - 1) % 19] == 12) {
          lengthOfAdarIAndII = 29;
        } else {
          lengthOfAdarIAndII = 59;
        }

        if (month == 4) {
          sdn = tishri1After + day - lengthOfAdarIAndII - 237;
        } else if (month == 5) {
          sdn = tishri1After + day - lengthOfAdarIAndII - 208;
        } else {
          sdn = tishri1After + day - lengthOfAdarIAndII - 178;
        }
        break;
      default:
        
        tishri1After = FindStartOfYear(year + 1, metonic, molad);

        switch (month) {
        case  7:
          sdn = tishri1After + day - 207;
          break;
        case  8:
          sdn = tishri1After + day - 178;
          break;
        case  9:
          sdn = tishri1After + day - 148;
          break;
        case 10:
          sdn = tishri1After + day - 119;
          break;
        case 11:
          sdn = tishri1After + day - 89;
          break;
        case 12:
          sdn = tishri1After + day - 60;
          break;
        case 13:
          sdn = tishri1After + day - 30;
          break;
        default:
          return(0);
        }
      }
      return(sdn + SDN_OFFSET);
  }
    
} 