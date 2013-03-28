
package genj.util;

import genj.util.swing.ImageIcon;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class LiturgicalYear {
  
  private final static Resources RES = Resources.get(LiturgicalYear.class);
  
  public final static ImageIcon IMAGE = new ImageIcon(LiturgicalYear.class, "LiturgicalYear.png");
  
  public final static String
    TXT_LITURGICAL_YEAR = RES.getString("liturgicalyear"),
    TXT_SUNDAY = RES.getString("liturgicalyear.sunday");

	public enum Sunday {
	  
    
		PostEpiphanias(Integer.MAX_VALUE, 5, "post Ephiphanias") { 
		  
		  
		  @Override
		  protected Calendar getDateImpl(int year, int postEventWeeks) {
	      int K;  
	      int S;  
	      int SZ; 
	      int dayOfYear;

	      
	      K = (year - 1) / 100;
	      S = 2 - (3*K + 3) / 4;
	      SZ = 7 - ((year - 1) + (year - 1)/4 + S) % 7; 

	      
	      dayOfYear = 7 + (7 - (313 - SZ) % 7) % 7 + (postEventWeeks-1) * 7; 
	      
        Calendar result = Calendar.getInstance();
	      result.set(Calendar.YEAR, year);
	      result.set(Calendar.DAY_OF_YEAR, dayOfYear);
	      return result;
		  }
		},
		
		Septuagesimae(-9, "Septuagesimae", "Circumdederunt"),
		Sexagesimae(-8, "Sexagesimae", "Exsurge"),
		Quinquagesimae(-7, "Qinquagesimae", "Estomihi"),
		Quadragesimae(-6, "Quadragesimae", "Invocavit"),
		Reminiscere(-5, "Reminiscere"),
		Oculi(-4, "Oculi"),
		Letare(-3, "Letare", "Laetare"),
		Judica(-2, "Judica"),
		Palmarum(-1, "Palmarum"),
		Paschale(0, "Paschale"),
		Quasimodogeniti(1, "Quasimodogeniti"),
		MisericordiasDomini(2, "Misericordias Domini"),
		Jubilate(3, "Jubilate"),
		Cantate(4, "Cantate"),
		Rogate(5, "Rogate"),
		Exaudi(6, "Exaudi"),
		Pentecost(7, "Pentecost"),
		Trinitatis(8, "Trinitatis"),
		PostTrinitatis(8, 27, "post Trinitatis"),
		
    
		Adventis(Integer.MAX_VALUE, 4, "Adventis") {
		  
      
      @Override
		  protected Calendar getDateImpl(int year, int week) {
		      int K;  
		      int S;  
		      int SZ; 
		      int dayOfNovember; 

		      
		      K = year / 100;
		      S = 2 - (3*K + 3) / 4;
		      SZ = 7 - (year + year/4 + S) % 7;

		      
		      dayOfNovember = 27 + (7 - (272-SZ) % 7) % 7 + (week-1) * 7;
		      
		      Calendar result = Calendar.getInstance();
		      result.set (Calendar.YEAR, year);
		      if (dayOfNovember > 30) {
		        result.set (Calendar.MONTH, 11);
		        result.set (Calendar.DAY_OF_MONTH, dayOfNovember - 30);
		      }
		      else {
		        result.set (Calendar.MONTH, 10);
		        result.set(Calendar.DAY_OF_MONTH, dayOfNovember);
		      }
		      
		      return result;
		  }
		};

    private int easterOffset;
		private int weeks;
		private List<String> names;
		
		private Sunday (int easterOffset, String... names) {
		  this.easterOffset = easterOffset;
			this.weeks = 0; 
			this.names = Collections.unmodifiableList(Arrays.asList(names));
		}

		private Sunday (int easterOffset, int weeks, String... names) {
      this.easterOffset = easterOffset;
			this.weeks = weeks;
			this.names = Collections.unmodifiableList(Arrays.asList(names));
		}

		
		public int getWeeks() {
			return weeks;
		}

    public String getName() {
      return names.get(0);
    }
    
		public List<String> getNames() {
			return names;
		}
		
		public Calendar getDate(int year, int relativeWeek) {
		  if (weeks>0&&relativeWeek<=0)
		    throw new IllegalArgumentException("bad relative week "+relativeWeek);
      if (relativeWeek>weeks)
        throw new IllegalArgumentException("bad relative week "+relativeWeek);
      return getDateImpl(year, relativeWeek);
		}
		
		protected Calendar getDateImpl(int year, int relativeWeek) {

		  
		  Calendar result = getEaster(year);

		  
      result.add(Calendar.DAY_OF_YEAR, (easterOffset+relativeWeek)*7);

      
		  return result;
		}

		
		public static Calendar getEaster(int year) {
		  
	    int K;  
	    int M;  
	    int S;  
	    int A;  
	    int D;  
	    int R;  
	    int OG; 
	    int SZ; 
	    int OE; 
	    int OS; 

	    K = year / 100;
	    M = 15 + (3*K + 3) / 4 - (8*K + 13) / 25;
	    S = 2 - (3*K + 3) / 4;
	    A = year % 19;
	    D = (19*A + M) % 30;
	    R = D/29 + (D/28 - D/29) * (A/11);
	    OG = 21 + D - R;
	    SZ = 7 - (year + year/4 + S) % 7;
	    OE = 7 - (OG - SZ) % 7;
	    OS = OG + OE;

	    Calendar result = Calendar.getInstance();
	    result.set (Calendar.YEAR, year);
	    if (OS > 31) {
	      result.set(Calendar.MONTH, 3);
	      result.set(Calendar.DAY_OF_MONTH, OS - 31);
	    }
	    else {
	      result.set(Calendar.MONTH, 2);
	      result.set(Calendar.DAY_OF_MONTH, OS);
	    }
	    return result;
	  }
		
	};

	
  public static void main(String[] args) {
    
    int year = 2009;
    int julianDay;
    
    Calendar date;

    for (LiturgicalYear.Sunday sunday: LiturgicalYear.Sunday.values()) {
      int i;
      if (sunday.getWeeks() > 0) i = 1;
      else i = 0;
      do {
        date = sunday.getDate(year, i);
        if (i > 0)
          System.out.format ("Julian Day for %d %s: %tD\n", i, sunday.getName(), date);
        else
          System.out.format ("Julian Day for %s: %tD\n", sunday.getName(), date);
        i++;
      } while (i <= sunday.getWeeks());
      }

  
    for (int i = 1982; i <= 2022; i+=1) {
      Calendar easterDate = Sunday.getEaster(i);
      System.out.format ("Easter Sunday in %d: %tD\n", i, easterDate);
    }
  
  }
}
