

import genj.chart.Chart;
import genj.chart.IndexedSeries;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyName;
import genj.gedcom.time.PointInTime;
import genj.report.Report;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class ReportNameHistory extends Report {

  
  public int prefixPresentation = PropertyName.PREFIX_AS_IS;
  public String[] prefixPresentations = { 
          
          translate("prefixAsIs"), 
          translate("prefixLast"), 
          translate("prefixIgnore") 
  };
  
  
  public boolean makeGroupOther = false;

  
  private int lifespanWithoutDEAT = 80;

  
  private float minUseOfName = 2;

  
  public float getMinUseOfName() {
    return minUseOfName;
  }

  
  public void setMinUseOfName(float set) {
    minUseOfName = Math.max(0, Math.min(set, 50));
  }

  
  public int getLifespanWithoutDEAT() {
    return lifespanWithoutDEAT;
  }

  
  public void setLifespanWithoutDEAT(int set) {
    lifespanWithoutDEAT = Math.max(20, Math.min(120, set));
  }

  
  public Chart start(Gedcom gedcom) {

    Collection indis = gedcom.getEntities(Gedcom.INDI);

    
    int
      start  = findStart(indis),
      length = PointInTime.getNow().getYear() - start + 1;

    
    IndexedSeries others = new IndexedSeries("", start, length);

    
    Map name2series = new TreeMap();
    Iterator iterator = indis.iterator();
    while (iterator.hasNext()) {
      Indi indi = (Indi)iterator.next();
      analyze(gedcom, indis, indi, name2series, others);
    }

    
    if (name2series.isEmpty())
      return null;

    
    if (makeGroupOther) {
        int numOthers = (PropertyName.getLastNames(gedcom, false).size()-name2series.size());
        if (numOthers>0) {
    	    others.setName(translate("others", numOthers));
    	    name2series.put(String.valueOf('\u'), others);
        }
    }

    
    return new Chart(translate("title", gedcom.getName()), null, translate("yaxis"), IndexedSeries.toArray(name2series.values()), new DecimalFormat("#"), true);

    
  }

  
  private int findStart(Collection indis) {
    
    int result = PointInTime.getNow().getYear()-100;
    
    Iterator it = indis.iterator();
    while (it.hasNext()) {
      Indi indi = (Indi)it.next();
      PropertyDate birth = indi.getBirthDate();
      if (birth!=null) {
        PointInTime start = birth.getStart();
        if (start.isValid()) try {
          
          result = Math.min(result, start.getPointInTime(PointInTime.GREGORIAN).getYear());
        } catch (GedcomException e) {
        }
      }
    }
    
    return result;
  }

  
  private void analyze(Gedcom gedcom, Collection indis, Indi indi, Map name2series, IndexedSeries others) {

    
	  PropertyName name = (PropertyName)indi.getProperty("NAME");
	  if (name==null||!name.isValid())
	    return;
	  String last = name.getLastName(prefixPresentation);
	  if (last.length()==0)
	    return;

	  
	  int start;
	  try {
	    start = indi.getBirthDate().getStart().getPointInTime(PointInTime.GREGORIAN).getYear();
	    if (start==PointInTime.UNKNOWN)
	      return;
	  } catch (Throwable t) {
	    return;
	  }

	  
	  int end = PointInTime.UNKNOWN;
	  try {
		  end = indi.getDeathDate().getStart().getPointInTime(PointInTime.GREGORIAN).getYear();
	  } catch (Throwable t) {
	  }
    if (end==PointInTime.UNKNOWN)
	    end = start+lifespanWithoutDEAT;

	  
	  IndexedSeries series;
	  if (name.getLastNameCount()<indis.size()*minUseOfName/100) {
	    if (!makeGroupOther)
	      return;
	    series = others;
	  } else {
		  series = (IndexedSeries)name2series.get(last);
		  if (series==null) {
		    series = new IndexedSeries(last, others);
		    name2series.put(last, series);
		  }
	  }

	  
	  for (;start<=end;start++)
	    series.inc(start);

	  
	}

} 