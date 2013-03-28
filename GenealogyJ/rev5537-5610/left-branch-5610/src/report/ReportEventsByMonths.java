
import genj.chart.Chart;
import genj.chart.IndexedSeries;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.time.Calendar;
import genj.gedcom.time.PointInTime;
import genj.report.Report;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ReportEventsByMonths extends Report {

  
  public boolean BirthsChart = true;
  
  public boolean BaptismsChart = false;
  
  public boolean AdoptionsChart = false;
  
  public boolean MarriagesChart = true;
  
  public boolean DivorcesChart = true;
  
  public boolean DeathsChart = true;
  
  public int inferiorYearLimit = 0;
  
  public int superiorYearLimit = 2100;
  
  
  private int calendar;

  
  public final static Calendar[] CALENDARS = {
    PointInTime.GREGORIAN,
    PointInTime.FRENCHR,
    PointInTime.JULIAN,
    PointInTime.HEBREW
  };

  
  public int getCalendar() {
    return calendar;
  }

  
  public void setCalendar(int set) {
    calendar = Math.max(0, Math.min(CALENDARS.length-1, set));
  }

  
  public Calendar[] getCalendars() {
    return CALENDARS;
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public Object start(Gedcom gedcom) {
    
	Map<String, String> labels = new HashMap<String, String>();
	  
    
    List<IndexedSeries> series = new ArrayList<IndexedSeries>();
    if (BirthsChart) {
    series.add(analyze(gedcom.getEntities("INDI"), "BIRT"));
    labels.put("BIRT",translate("birt"));
    }
    if (BaptismsChart) {
    series.add(analyze(gedcom.getEntities("INDI"), "BAPM"));
    labels.put("BAPM",translate("bapm"));
    }
    if (AdoptionsChart) {
    series.add(analyze(gedcom.getEntities("INDI"), "ADOP"));
    labels.put("ADOP",translate("adop"));
    }
    if (MarriagesChart) {
    series.add(analyze(gedcom.getEntities("FAM" ), "MARR"));
    labels.put("MARR",translate("marr"));
    }
    if (DivorcesChart) {
    series.add(analyze(gedcom.getEntities("FAM" ), "DIV"));
    labels.put("DIV",translate("div"));
    }
    if (DeathsChart) {
    series.add(analyze(gedcom.getEntities("INDI"), "DEAT"));
    labels.put("DEAT",translate("deat"));
    }

    
    String[] categories = CALENDARS[calendar].getMonths(true);

    JTabbedPane charts = new JTabbedPane();
    for (Iterator<IndexedSeries> it=series.iterator(); it.hasNext(); ) {
      IndexedSeries is = (IndexedSeries)it.next();
      
      String label = (String)labels.get(is.getName());
      
      String[] chartTitleParameters = {label, new Integer(inferiorYearLimit).toString(),new Integer(superiorYearLimit).toString()};
      Chart chart = new Chart(translate("chart.title",chartTitleParameters), is, categories, false);
      
      charts.addTab(label, chart);
    }
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(BorderLayout.CENTER, charts);
    return panel;

    
  }

  private IndexedSeries analyze(Collection entities, String tag) {

    int months = CALENDARS[calendar].getMonths(true).length;

    IndexedSeries series = new IndexedSeries(tag, months);

    
    Iterator it = entities.iterator();
    while (it.hasNext()) {

      Entity e = (Entity)it.next();

      
      Property event = e.getProperty(series.getName());
      if (!(event instanceof PropertyEvent))
        continue;
      PropertyDate date = ((PropertyEvent)event).getDate();
      if (date==null)
        continue;

      
      try {
    	  if(date.getStart().getPointInTime(CALENDARS[calendar]).getYear()<inferiorYearLimit || 
    			  date.getStart().getPointInTime(CALENDARS[calendar]).getYear()>superiorYearLimit){
    		 
    	  }
    	  else{
    		  series.inc(date.getStart().getPointInTime(CALENDARS[calendar]).getMonth());
    	  }
    	  
        
      } catch (Throwable t) {
      }

      
    }

    
    return series;
  }

} 
