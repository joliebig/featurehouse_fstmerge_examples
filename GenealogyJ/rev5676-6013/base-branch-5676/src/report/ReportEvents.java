

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.time.PointInTime;
import genj.report.Report;
import genj.util.WordBuffer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;


public class ReportEvents extends Report {
  
    
    public boolean isSortDay = true;
    
    public boolean reportBirth = true;
    
    public boolean reportBaptism = true;
    
    public boolean reportMarriage = true;
    
    public boolean reportDivorce = true;
    
    public boolean reportEmigration = true;
    
    public boolean reportImmigration = true;
    
    public boolean reportNaturalization = true;
    
    public boolean reportDeath = true;
    
    public boolean isOutputICal = false;

    
    public boolean isNoDead = false;
    
    
    public String place = "";
    
    
    public int sex = 3;
    public String[] sexs = {PropertySex.TXT_MALE, PropertySex.TXT_FEMALE, PropertySex.TXT_UNKNOWN, ""};

    
    public String day = "";

    
    public String month = "";

    
    public String year = "";

    
    private final static String TXT_MARR_SYMBOL = genj.gedcom.Options.getInstance().getTxtMarriageSymbol();
    
    
    private String timestamp;
    private final static SimpleDateFormat 
      formatDTSTAMP = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'"),
      formatDTSTART = new SimpleDateFormat("yyyyMMdd");


    
    public void start(Gedcom gedcom) throws GedcomException {

        
        if ((!reportBirth) && (!reportBaptism) && (!reportDeath) && (!reportMarriage) && (!reportDivorce) && (!reportEmigration) && (!reportImmigration) && (!reportNaturalization))
            return;
        
        
        timestamp = toISO(System.currentTimeMillis(), true);
        formatDTSTART.setTimeZone(TimeZone.getTimeZone("GMT")); 
        formatDTSTAMP.setTimeZone(TimeZone.getDefault()); 

        
        Map<String, List<Hit>> tag2events = new HashMap<String, List<Hit>>();
        if (reportBirth) tag2events.put("BIRT", new ArrayList<Hit>());
        if (reportBaptism) tag2events.put("BAPM|BAPL|CHR|CHRA",  new ArrayList<Hit>());
        if (reportMarriage) tag2events.put("MARR", new ArrayList<Hit>());
        if (reportDivorce) tag2events.put("DIV", new ArrayList<Hit>());
        if (reportEmigration) tag2events.put("EMI", new ArrayList<Hit>());
        if (reportImmigration) tag2events.put("IMMI", new ArrayList<Hit>());
        if (reportNaturalization) tag2events.put("NATU", new ArrayList<Hit>());
        if (reportDeath) tag2events.put("DEAT", new ArrayList<Hit>());

        
        for (Entity indi : (Collection<Entity>)gedcom.getEntities(Gedcom.INDI)) {
            analyze((Indi)indi, tag2events);
        }
        
        
        if (isOutputICal) {
          println("BEGIN:VCALENDAR");
          println("PRODID:-//Genealogy//ReportEvents/EN");
          println("VERSION:2.0");
          println("METHOD:PUBLISH");
        } else {
          if (isNoDead)
            println(translate("isNoDead"));
          if (place.length()>0)
            println(translate("place") + " = " + place);
          if (sex!=3)
            println(translate("sex") + " = " + sexs[sex]);
          if (day.length()>0)
            println(translate("day") + " = " + day);
          if (month.length()>0)
            println(translate("month") + " = " + month);
          if (year.length()>0)
            println(translate("year")+ " = " + year);
          println();
        }
        
        
        for (String tag : tag2events.keySet()) {
          List<Hit> events = tag2events.get(tag);
          if (!events.isEmpty()) {
            
            Collections.sort(events);
            
            if (!isOutputICal) 
              println(getIndent(2) + Gedcom.getName(new StringTokenizer(tag, "|").nextToken()));

            for (Hit event : events) 
              println(event);
            
            if (!isOutputICal) 
              println();
            
            events.clear();
          }
        }
        
        
        if (isOutputICal) {
          println("END:VCALENDAR");
        }

        
    }

    
    private void analyze(Indi indi, Map<String,List<Hit>> tag2events) {

        
        if (isNoDead && indi.getDeathDate() != null && indi.getDeathDate().isValid())
            return;

        if(checkSex(indi)==false)
            return;

        
        analyzeEvents(indi, tag2events);
        
        
        Fam[] fams = indi.getFamiliesWhereSpouse();
        for (int f = 0; f < fams.length; f++) {
          Fam fam = fams[f];
          analyzeEvents(fam, tag2events);
        }

        
    }
    
    private void analyzeEvents(Entity entity, Map<String, List<Hit>> tag2events) {
      
      for (String tag : tag2events.keySet()) {
        List<Hit> events = tag2events.get(tag);
        
        Property[] props = getProperties(entity, tag);
        for (int i = 0; i < props.length; i++) {
          Property event = props[i];
          if (event instanceof PropertyEvent) {
            Property date = event.getProperty("DATE");
            if (date instanceof PropertyDate) {
              Hit hit = new Hit(entity, (PropertyEvent)event, i, (PropertyDate)date);
              
              
              if (events.contains(hit))
                continue;
              
              if (!checkDate((PropertyDate)date))
                continue;
              
              if (!checkPlace(event))
                continue;
              
              events.add(hit); 
            }
          }
        }
      }
      
    }
    
    private Property[] getProperties(Entity entity, String tag) {
      ArrayList<Property> result = new ArrayList<Property>();
      for (int i=0, j = entity.getNoOfProperties(); i<j ; i++) {
        Property prop = entity.getProperty(i);
        if (prop.getTag().matches(tag))
          result.add(prop);
      }
      return Property.toArray(result);
    }
    
    
    private boolean checkPlace(Property event) {
      
      
      if (place.length()==0)
        return true;
      
      
      PropertyPlace pp = (PropertyPlace)event.getProperty("PLAC");
      if (pp==null)
        return false;
      
      
      return pp.getValue().matches("(?i).*"+place+".*");
    }

    
    private boolean checkSex(Indi indi) {

        if(sex==3)
            return true;

        switch(indi.getSex()) {

            case PropertySex.MALE:
                if(sex!=0)
                    return false;
                else
                    return true;

            case PropertySex.FEMALE:
                if(sex!=1)
                    return false;
                else
                    return true;

            case PropertySex.UNKNOWN:
                if(sex!=2)
                    return false;
                else
                    return true;

            default: return false;
        }
    }

    
    private boolean checkDate(PropertyDate date) {

        
        if (date==null||!date.isValid())
            return false;

        PointInTime start = date.getStart();
        if (start.getCalendar()!=PointInTime.GREGORIAN)
            return false;
        
        if (checkValue(start.getDay()+1, day) && checkValue(start.getMonth()+1, month) && checkValue(start.getYear(), year))
          return true;

        return false; 
    }
    
    private boolean checkValue(int value, String filter) {
      
      if (filter.length()==0)
        return true;
      
      try {
        
        if (filter.startsWith(">"))
          return Integer.parseInt(filter.substring(1)) < value;
        
        if (filter.charAt(0)=='<')
          return Integer.parseInt(filter.substring(1)) > value;
        
        if (filter.charAt(0)=='=')
          return Integer.parseInt(filter.substring(1)) == value;
        
        int range = filter.indexOf("-");
        if (range>0) {
          return Integer.parseInt(filter.substring(0,range)) <= value
            && Integer.parseInt(filter.substring(range+1)) >= value;
        }
        return Integer.parseInt(filter) == value;
      } catch (NumberFormatException e) {
        return false;
      }
    }
    
    private String toISO(long timeMillis, boolean time) {
      Date date = new Date(timeMillis);
      return time ? formatDTSTAMP.format(date) : formatDTSTART.format(date);
    }        

    
    private class Hit implements Comparable<Hit> {
      
      Entity who;
      int num;
      Property event;
      PropertyDate date;
      PointInTime compare;
        
      
      Hit(Entity entity, PropertyEvent event, int num, PropertyDate date) {
        this.who = entity;
        this.event = event;
        this.num = num;
        this.date = date;
        PointInTime when = date.getStart();
        if (isSortDay)
          
          compare = new PointInTime(when.getDay(), when.getMonth(), 4, when.getCalendar());
        else
          compare = when;
      }
      
      public int compareTo(Hit other) {
          return compare.compareTo(other.compare);
      }
      
      public boolean equals(Object that) {
        return event==((Hit)that).event;
      }
      
      public String toString() {
        try {
          if (isOutputICal) {
            WordBuffer result = new WordBuffer("\n");
            result.append("BEGIN:VEVENT");
            result.append("DTSTART:"+toISO(date.getStart().getTimeMillis(), false));
            result.append("UID:"+who.getGedcom().getName()+"|"+who.getId()+"|"+event.getTag()+"|"+num);
            result.append("DTSTAMP:"+timestamp);
            result.append("SUMMARY:"+Gedcom.getName(event.getTag())+" "+icalescape(who.toString()));
            Property where  = event.getProperty("PLAC");
            if (where!=null)
              result.append("LOCATION:"+icalescape(where.getDisplayValue()));
            if (event.getTag().equals("BIRT"))
              result.append("RRULE:FREQ=YEARLY");
            result.append("DESCRIPTION:");
            result.append("END:VEVENT");
            return result.toString();
          } else {
            StringBuffer result = new StringBuffer();
            result.append(getIndent(3));
            result.append(date);
            result.append(" ");
            result.append(who);
            return result.toString();
          }
        } catch (Throwable t) {
          throw new RuntimeException();
        }
      }
      private String icalescape(String string) {
        return string.replaceAll(",", "\\\\,");
      }
    } 

} 
