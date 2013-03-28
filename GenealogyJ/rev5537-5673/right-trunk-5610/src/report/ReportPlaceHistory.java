

import genj.chart.Chart;
import genj.chart.XYSeries;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyPlace;
import genj.gedcom.time.PointInTime;
import genj.report.Report;
import genj.util.DirectAccessTokenizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ReportPlaceHistory extends Report {

  private final static String
  	PLAC = "PLAC",
  	DATE = "DATE";

  
  private int topn = 10;

  
  private int resolution = 10;

  
  public int getTopn() {
    return topn;
  }

  
  public void setTopn(int set) {
    topn = Math.max(0, Math.max(3, set));
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Gedcom gedcom) {

    
    int jurisdiction = 0;
    String format  = gedcom.getPlaceFormat();
    if (format.length()>0) {
      String [] jurisdictions = new DirectAccessTokenizer(format,PropertyPlace.JURISDICTION_SEPARATOR).getTokens(true);
      if (jurisdictions.length>0) {
        Object choice = getValueFromUser(translate("jurisdiction"), jurisdictions, jurisdictions[0]);
        if (choice==null) return;
        for (int i=0; i<jurisdictions.length; i++) if (jurisdictions[i]==choice) jurisdiction = i;
      }
    }

    
    Map plac2series = getSeriesForPlaces(gedcom, jurisdiction);

    
    Iterator indis = gedcom.getEntities(Gedcom.INDI).iterator();
    while (indis.hasNext()) {
      analyze((Indi)indis.next(), plac2series, jurisdiction);
    }

    
    Iterator fams = gedcom.getEntities(Gedcom.FAM).iterator();
    while (fams.hasNext()) {
      analyze((Fam)fams.next(), plac2series, jurisdiction);
    }

    
    XYSeries[] series = new XYSeries[plac2series.size()];
    plac2series.values().toArray(series);

    
    String title = translate("title", gedcom.getName());
    String xaxis = translate("xaxis", resolution);
    String yaxis = translate("yaxis");
    Chart chart = new Chart(title, xaxis, yaxis, series, null, true);
    showChartToUser(chart);

    
  }

  
  private void analyze(Entity ent, Map plac2series, int jurisdiction) {

    
    Iterator it = ent.getProperties(PropertyPlace.class).iterator();
    while (it.hasNext()) {
      
      PropertyPlace place = (PropertyPlace)it.next();
      XYSeries series = (XYSeries)plac2series.get(place.getJurisdiction(jurisdiction));
      if (series==null)
        continue;
      
      Property parent = place.getParent();
      Property date = parent.getProperty(DATE);
      if (!(date instanceof PropertyDate))
        continue;
      
      try {
	      int year = ((PropertyDate)date).getStart().getPointInTime(PointInTime.GREGORIAN).getYear();
	      if (year!=PointInTime.UNKNOWN) {
	        series.inc( year/resolution*resolution );
            System.out.println(year);
          }
      } catch (GedcomException e) {
        
      }
    }

    
  }

  
  private Map getSeriesForPlaces(Gedcom gedcom, int jurisdiction) {

    
    String[] jurisdictions = PropertyPlace.getAllJurisdictions(gedcom, jurisdiction, false);

    
    Map result = new HashMap();
    for (int s=jurisdictions.length-1, i=0; s>=0&&i<topn; s--,i++) {
      result.put(jurisdictions[s], new XYSeries(jurisdictions[s]));
    }

    
    return result;
  }

} 
