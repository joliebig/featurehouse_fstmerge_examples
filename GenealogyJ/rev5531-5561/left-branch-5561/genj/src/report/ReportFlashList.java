
import genj.fo.Document;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyPlace;
import genj.gedcom.TagPath;
import genj.gedcom.time.PointInTime;
import genj.report.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


public class ReportFlashList extends Report {

  private final static TagPath CITY = new TagPath(".:ADDR:CITY");
  
  

  private final static String FORMAT_LNORMAL = "font-weight=normal,text-align=left";
  private final static String FORMAT_CNORMAL = "font-weight=normal,text-align=center";
  private final static String FORMAT_RNORMAL = "font-weight=normal,text-align=right";
  private final static String FORMAT_LSTRONG = "font-weight=bold,text-align=left";
  private final static String FORMAT_CSTRONG = "font-weight=bold,text-align=center";
  private final static String FORMAT_RSTRONG = "font-weight=bold,text-align=right";
  private final static String FORMAT_CBACKGROUND = "background-color=#ffffcc,font-weight=bold,text-align=center";
  private final static String FORMAT_RBACKGROUND = "background-color=#ffffcc,font-weight=bold,text-align=right";

  
  public boolean weAddaTOC = true;

  
  public int     displayLegend = LEGEND_BOT;
  
  private final static int
    LEGEND_NO = 0,
    LEGEND_TOP = 1,
    LEGEND_BOT = 2;

  public String displayLegends[] = {
      translate("legendNone"),
      translate("legendTop"),
      translate("legendBot")
   };

  
  public String FilterKey1 = "*"; 
  public String FilterKey2 = "*"; 
  public String FilterKey3 = "*"; 
  
  
  public boolean repeatHeader = true; 
  public boolean repeatKeys = false; 
  
  
  public boolean displayZeros = false;
  
  
  public int nbEvents = 3;          
  public int yearSpan = 50;         
  public int minSosa = 1;           

  
  public int counterIncrement = 2;
  public String counterIncrements[] = { translate("DoNotShow"), "10", "100", "1000", "10000" };
  
  
  private int posLoc1 = 0; 
  private int posLoc2 = 2;
  private int posLoc3 = 4;
  
  private boolean existPLACTag = true;  

  private final static int
    LOC12_SURN_LOC3 = 0,
    LOC12_LOC3_SURN = 1,
    SURN_LOC12_LOC3 = 2,
    SURN_LOC1_LOC23 = 3,
    LOC1_LOC23_SURN = 4,
    LOC1_SURN_LOC23 = 5;

  private int recordKey = LOC12_SURN_LOC3;  

  private String recordKeyText = "";        

  
  protected ImageIcon getImage() {
    return Report.IMG_FO;
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Gedcom gedcom) {
    start(gedcom, gedcom.getEntities(Gedcom.INDI), null);
  }

  
  public void start(Indi[] indis) {
    start(indis[0].getGedcom(), Arrays.asList(indis), null);
  }

  
  public void start(Indi indi) {
    start(indi.getGedcom(), indi.getGedcom().getEntities(Gedcom.INDI), indi);
  }

  
  private void start(Gedcom gedcom, Collection indis, Indi indiDeCujus) {

    
    
    if (!getFlashOptions(gedcom)) return;
    
    
    if (indiDeCujus == null) {
      String msg = translate("AskDeCujus");
      indiDeCujus = (Indi)getEntityFromUser(msg, gedcom, Gedcom.INDI);
      if (indiDeCujus == null) return;
      }

    
    Map primary = new TreeMap();
    int countIndiv = 0;
    counterIncrement = (int)Math.pow(10, counterIncrement);
    println(translate("StartingAnalysis"));      
    for (Iterator it = indis.iterator(); it.hasNext();) {
      analyze(  (Indi) it.next(), primary, indiDeCujus);
      if (counterIncrement > 1) {
         countIndiv++;
         if ((int)Math.floor(countIndiv/counterIncrement) * counterIncrement == countIndiv)
            println(String.valueOf(countIndiv));      
         }
      }
    if (counterIncrement > 1) 
      println(translate("NowWriting")+"...");      

    
    Document doc = new Document(getName());
        
    
    if (weAddaTOC) {
      doc.addTOC();
      if (primary.size()>10) doc.nextPage();
    }
    
    
    if (displayLegend == LEGEND_TOP) {
      displayLegend(doc);
    }
    
    
    if (!repeatHeader) {
       displayHeader(doc, null, true);
       doc.endTable();
       }

    
    
    
    for (Iterator ps = primary.keySet().iterator(); ps.hasNext(); ) {
      String p = (String)ps.next();

      
      
      doc.startSection(p,p.replaceAll(" ","%").replaceAll("/","%").replaceAll(",","%")); 
      displayHeader(doc, p, repeatHeader);
      String secondaryKey = "";
      
      Map secondary = (Map)lookup(primary, p, null);
      for (Iterator ss = secondary.keySet().iterator(); ss.hasNext(); ) {
        String s = (String)ss.next();
        Map tertiary = (Map)lookup(secondary, s, null);
        for (Iterator ts = tertiary.keySet().iterator(); ts.hasNext(); ) {
          String t = (String)ts.next();
          Range range = (Range)lookup(tertiary, t, null);
           
          String lformat = FORMAT_LNORMAL;
          String cformat = FORMAT_CNORMAL;
          String rformat = FORMAT_RNORMAL;
          if ((range.getNbEvents()  >= nbEvents) ||
              (range.getYearSpan()  >= yearSpan) ||
              (range.getValueSosa() >= minSosa)) {
             lformat = FORMAT_LSTRONG;
             cformat = FORMAT_CSTRONG;
             rformat = FORMAT_RSTRONG;
             }
          
          doc.nextTableRow(lformat);
          if (repeatKeys == true || secondaryKey != s)
             doc.addText(s);
          else    
             doc.addText(" ");
          if (secondaryKey != s) secondaryKey = s;
          doc.nextTableCell(lformat);
          doc.addText(t);
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbSosa == 0)) ? "-" : range.getNbSosa());
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbBirth == 0)) ? "-" : range.getNbBirth());
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbChris == 0)) ? "-" : range.getNbChris());
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbMarr == 0)) ? "-" : range.getNbMarr());
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbDeath == 0)) ? "-" : range.getNbDeath());
          doc.nextTableCell(rformat);
          doc.addText((!displayZeros && (range.nbOther == 0)) ? "-" : range.getNbOther());
          doc.nextTableCell(cformat);
          doc.addText(range.getFirst());
          doc.nextTableCell(cformat);
          doc.addText(range.getLast());
        }
      }
      doc.endTable();
      
    }

    
    if (displayLegend == LEGEND_BOT) {
      displayLegend(doc);
    }
    
    
    println(translate("Completed"));      
    showDocumentToUser(doc);
  
  } 

  
  
  private boolean getFlashOptions(Gedcom gedcom) {
    
    
    if (gedcom.getPlaceFormat() == "") {
      
      String[] choices = { translate("SurnLoc"), translate("LocSurn") };
      recordKeyText  = (String)getValueFromUser(translate("recordKey"), choices, choices[0]);
      existPLACTag = false;
      if (recordKeyText  == null) { return false; } 
      if (recordKeyText.compareTo(translate("LocSurn")) == 0) { 
          recordKey = LOC1_SURN_LOC23; 
          } 
      else { 
          recordKey = SURN_LOC1_LOC23; 
          }
      } 
      
      else {
      String recordKeys[] = {
         translate("loc1")+"/"+translate("loc2")+" > "+translate("surname")+" > " +translate("loc3"),
         translate("loc1")+"/"+translate("loc2")+" > "+translate("loc3")+" > "+translate("surname"),
         translate("surname")+" > "+translate("loc1")+"/"+translate("loc2")+" > "+translate("loc3"),
         translate("surname")+" > "+translate("loc1")+" > "+translate("loc2")+"/"+translate("loc3"),
         translate("loc1")+" -> "+translate("loc2")+"/"+translate("loc3")+" > "+translate("surname"),
         translate("loc1")+" -> "+translate("surname")+" > "+translate("loc2")+"/"+translate("loc3")
         };
      recordKeyText = (String)getValueFromUser(translate("recordKey"), recordKeys, recordKeys[0]);
      if (recordKeyText == null) { return false; } 
         else {
           List table = Arrays.asList(recordKeys); 
           recordKey = table.indexOf(recordKeyText);
         }
      
      List tag = Arrays.asList(gedcom.getPlaceFormat().split("\\,")); 
      ArrayList choices = new ArrayList((Collection)Arrays.asList(gedcom.getPlaceFormat().split("\\,"))); 
      
      recordKeyText = recordKeyText.replaceAll(translate("loc1"),"XXX");
      String selection1 = (String)getValueFromUser(translate("recordKey1")+" "+recordKeyText, (Object[])choices.toArray(), choices.get(0));
      if (selection1 == null) { return false; } 
         else { choices.remove(choices.indexOf(selection1)); }
      
      recordKeyText = recordKeyText.replaceAll("XXX",selection1);
      recordKeyText = recordKeyText.replaceAll(translate("loc2"),"XXX");
      String selection2 = (String)getValueFromUser(translate("recordKey2")+" "+recordKeyText, (Object[])choices.toArray(), choices.get(0));
      if (selection2 == null) { return false; } 
         else { choices.remove(choices.indexOf(selection2)); }
      
      recordKeyText = recordKeyText.replaceAll("XXX",selection2);
      recordKeyText = recordKeyText.replaceAll(translate("loc3"),"XXX");
      String selection3 = (String)getValueFromUser(translate("recordKey3")+" "+recordKeyText, (Object[])choices.toArray(), choices.get(0));
      if (selection3 == null) { return false; } 
         else { choices.remove(choices.indexOf(selection3)); }
      recordKeyText = recordKeyText.replaceAll("XXX",selection3);

      int[] list = { 
            tag.indexOf(selection1), 
            tag.indexOf(selection2), 
            tag.indexOf(selection3) 
            };
      posLoc1 = list[0];
      posLoc2 = list[1];
      posLoc3 = list[2];
      existPLACTag = true;
      }
    return true;  
    
  }

  
  
  private void displayLegend(Document doc) {
    
    
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.startSection(translate("legendText"), "legend");
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(translate("recordKey")+" "+recordKeyText);  doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(translate("legendS"));     doc.nextParagraph();
    doc.addText(translate("legendB"));     doc.nextParagraph();
    doc.addText(translate("legendC"));     doc.nextParagraph();
    doc.addText(translate("legendM"));     doc.nextParagraph();
    doc.addText(translate("legendD"));     doc.nextParagraph();
    doc.addText(translate("legendO"));     doc.nextParagraph();
    doc.addText(translate("legendMin"));   doc.nextParagraph();
    doc.addText(translate("legendMax")); 
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    doc.addText(" ");     doc.nextParagraph();
    
  }

   
  private void displayHeader(Document doc, String name, boolean dHeader) {
    
    if (name!=null)
      doc.startTable("genj:csv=true,genj:csvprefix="+name+",width=100%");
    else
      doc.startTable("width=100%");
    
      doc.addTableColumn("column-width=30%");     
      doc.addTableColumn("column-width=23%");     
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=5%");      
      doc.addTableColumn("column-width=10%");     
      doc.addTableColumn("column-width=7%");     

      
      if (dHeader) {
         doc.nextTableRow();
         doc.addText(" ");
         doc.nextTableCell();
         doc.addText(" ");
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colS"));
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colB"));
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colC"));
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colM"));
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colD"));
         doc.nextTableCell(FORMAT_RBACKGROUND);
         doc.addText(translate("colO"));
         doc.nextTableCell(FORMAT_CBACKGROUND);
         doc.addText(translate("colMin"));
         doc.nextTableCell(FORMAT_CBACKGROUND);
         doc.addText(translate("colMax"));
      }
    
  }
  
  
  private void analyze(Indi indi, Map primary, Indi indiDeCujus) {

    
    String name = indi.getLastName();
    if (name.length()==0)
      return;
    name = name.trim();

    
    boolean isSosa = indi.isAncestorOf(indiDeCujus);
    if (indi == indiDeCujus) isSosa = true;
     
    try {
       
       for (Iterator dates = indi.getProperties(PropertyDate.class).iterator(); dates.hasNext(); ) {
         
         PropertyDate date = (PropertyDate)dates.next();
         if (!date.isValid()) continue;
         
         int start = 0;
         start = date.getStart().getPointInTime(PointInTime.GREGORIAN).getYear();
         int end = date.isRange() ? date.getEnd().getPointInTime(PointInTime.GREGORIAN).getYear() : start;
         if (start>end) continue;
         
         analyzePlaces(name, start, end, date.getParent(), primary, isSosa);
         
         analyzeCities(name, start, end, date.getParent(), primary, isSosa);

         
       }

       
       for (Iterator families = Arrays.asList(indi.getFamiliesWhereSpouse()).iterator();
       families.hasNext(); ) {
         Fam family = (Fam)families.next();
            for (Iterator dates = family.getProperties(PropertyDate.class).iterator(); dates.hasNext(); ) {
           
           PropertyDate date = (PropertyDate)dates.next();
           if (!date.isValid()) continue;
           
           int start = date.getStart().getPointInTime(PointInTime.GREGORIAN).getYear();
           int end = date.isRange() ? date.getEnd().getPointInTime(PointInTime.GREGORIAN).getYear() : start;
           if (start>end) continue;
           
           analyzePlaces(name, start, end, date.getParent(), primary, isSosa);
           
           analyzeCities(name, start, end, date.getParent(), primary, isSosa);
         } 
       } 

    } catch (Throwable t) {
      t.printStackTrace();
    }
    
    
  }

  
  private void analyzeCities(String name, int start, int end, Property prop, Map primary, boolean isSosa) {
    
    if (!isEvent(prop)) return;
      
    Property[] cities = prop.getProperties(CITY);
    for (int c = 0; c < cities.length; c++) {
      
      String loc1 = cities[c].getDisplayValue().trim();
      if (loc1.length()==0) continue;
      
      
      
      
      String loc2 = "";
      String loc3 = "";
      if (existPLACTag) { loc2 = "-"; loc3 = "-"; }
      
      keep(loc1, loc2, loc3, name, start, end, primary, prop, isSosa);
      
    }
    
  }

  
  private void analyzePlaces(String name, int start, int end, Property prop, Map primary, boolean isSosa) {
    
    if (!isEvent(prop)) return;
      
    
    for (Iterator places = prop.getProperties(PropertyPlace.class).iterator(); places.hasNext(); ) {
      
      PropertyPlace place = (PropertyPlace)places.next();
      
      
      String loc1 = "";
      String loc2 = "";
      String loc3 = "";
      if (existPLACTag) {
         loc1 = place.getJurisdiction(posLoc1);
         loc2 = place.getJurisdiction(posLoc2);
         loc3 = place.getJurisdiction(posLoc3);
         if (loc1 != null) loc1 = loc1.trim();
         if (loc2 != null) loc2 = loc2.trim();
         if (loc3 != null) loc3 = loc3.trim();
         if ((loc1 == null) || (loc1.length() == 0)) loc1 = "-";
         if ((loc2 == null) || (loc2.length() == 0)) loc2 = "-";
         if ((loc3 == null) || (loc3.length() == 0)) loc3 = "-";
         
         if ((loc1 == "-") && (loc2 == "-") && (loc3 == "-")) continue;
         }
      else {
         
         loc1 = place.getFirstAvailableJurisdiction().trim();
         
         if (loc1.length()==0) continue;
         }
      
      keep(loc1, loc2, loc3, name, start, end, primary, prop, isSosa);
      
    }
    
  }

  
  private boolean isEvent(Property prop) {
    
    String strTable[] = { "ADOP", "ANUL", "BIRT", "BAPM", "BARM", "BASM", "BLES", "BURI", "CENS", "CHR", "CHRA", "CONF", "CREM", "DEAT", "DIV", "DIVF", "EMIG", "ENGA", "EVEN", "FCOM", "GRAD", "IMMI", "MARR", "MARB", "MARC", "MARL", "MARS", "NATU", "ORDN", "RETI", "PROB", "WILL" };
    List listOfEvents = Arrays.asList(strTable);
    return listOfEvents.contains(prop.getTag());
    
  }

  private void keep(String loc1, String loc2, String loc3, String name, int start, int end, Map primary, Property prop, boolean isSosa) {

    
    String ps, ss, ts;
    switch (recordKey) {
      case LOC12_SURN_LOC3:
        ps = loc1+"/"+loc2;
        ss = name;
        ts = loc3;
        break;
      case LOC12_LOC3_SURN:
        ps = loc1+"/"+loc2;
        ss = loc3;
        ts = name;
        break;
      case SURN_LOC12_LOC3:
        ps = name;
        ss = loc1+"/"+loc2;
        ts = loc3;
        break;
      case SURN_LOC1_LOC23:
        ps = name;
        ss = loc1;
        ts = loc2+"/"+loc3;
        break;
      case LOC1_LOC23_SURN:
        ps = loc1;
        ss = loc2+"/"+loc3;
        ts = name;
        break;
      case LOC1_SURN_LOC23:
        ps = loc1;
        ss = name;
        ts = loc2+"/"+loc3;
        break;
      default:
        throw new IllegalArgumentException("no such report type");
    }

    
    if ((FilterKey1.trim().compareTo("*") != 0) && (ps.toUpperCase().indexOf(FilterKey1.toUpperCase()) == -1))
       return; 
    if ((FilterKey2.trim().compareTo("*") != 0) && (ss.toUpperCase().indexOf(FilterKey2.toUpperCase()) == -1))
       return; 
    if ((FilterKey3.trim().compareTo("*") != 0) && (ts.toUpperCase().indexOf(FilterKey3.toUpperCase()) == -1))
       return; 
           
    
    if (!existPLACTag) { ts = ""; }
    Map secondary = (Map)lookup(primary, ps, TreeMap.class);
    Map tertiary  = (Map)lookup(secondary, ss, TreeMap.class);
    Range range = (Range)lookup(tertiary, ts, Range.class);
    range.add(start, end, isSosa, prop.getTag());
    
  }

  
  private Object lookup(Map index, String key, Class fallback) {
    
    Object result = index.get(key);
    if (result==null) {
      try {
        result = fallback.newInstance();
      } catch (Throwable t) {
        t.printStackTrace();
        throw new IllegalArgumentException("can't instantiate fallback "+fallback);
      }
      index.put(key, result);
    }
    
    return result;
  }

  
  static class Range {
    int firstYear = Integer.MAX_VALUE, 
        lastYear = -Integer.MAX_VALUE,
        nbSosa   = 0,
        nbBirth  = 0,
        nbChris  = 0,
        nbMarr   = 0,
        nbDeath  = 0,
        nbOther  = 0;
    double 
        geoLat   = 0,
        geoLon   = 0;

    void add(int start, int end, boolean isSosa, String tag) {
      
      if (start!=PointInTime.UNKNOWN)
        firstYear = Math.min(firstYear, start);
      if (end!=PointInTime.UNKNOWN)
        lastYear = Math.max(lastYear, end);
      if (isSosa) 
        nbSosa++;
      if (tag == "BIRT") nbBirth++;
         else 
      if (tag == "CHR") nbChris++;
         else 
      if (tag == "MARR") nbMarr++;
         else 
      if (tag == "DEAT") nbDeath++;
         else nbOther++;
    }
    
    void add(Range rangeElt) {
      firstYear = Math.min(firstYear, rangeElt.getValueFirst());
      lastYear  = Math.max(lastYear, rangeElt.getValueLast());
      nbSosa += rangeElt.getValueSosa();
      nbBirth += rangeElt.getValueBirth();
      nbChris += rangeElt.getValueChris();
      nbMarr += rangeElt.getValueMarr();
      nbDeath += rangeElt.getValueDeath();
      nbOther += rangeElt.getValueOther();
    }
    
    void setGeo(double lat, double lon) {
      geoLat = lat;
      geoLon = lon;
    }
    
    String getFirst() {
      
      if (firstYear==Integer.MAX_VALUE|| lastYear==Integer.MAX_VALUE)
        return "";
      return Integer.toString(firstYear);
    }

    String getLast() {
      return Integer.toString(lastYear);
    }
  
    String getNbSosa() {
      return Integer.toString(nbSosa);
    }
  
    String getNbBirth() {
      return Integer.toString(nbBirth);
    }
  
    String getNbChris() {
      return Integer.toString(nbChris);
    }
  
    String getNbMarr() {
      return Integer.toString(nbMarr);
    }
  
    String getNbDeath() {
      return Integer.toString(nbDeath);
    }
    
    String getNbOther() {
      return Integer.toString(nbOther);
    }
    
    int getNbEvents() {
      return (nbBirth+nbChris+nbMarr+nbDeath+nbOther);
    }
  
    int getYearSpan() {
      return (lastYear-firstYear);
    }
  
    int getValueFirst() {
      return (firstYear);
    }
  
    int getValueLast() {
      return (lastYear);
    }
  
    int getValueSosa() {
      return (nbSosa);
    }
  
    int getValueBirth() {
      return (nbBirth);
    }
  
    int getValueChris() {
      return (nbChris);
    }
  
    int getValueMarr() {
      return (nbMarr);
    }
  
    int getValueDeath() {
      return (nbDeath);
    }
  
    int getValueOther() {
      return (nbOther);
    }
  
    double getValueLat() {
      return (geoLat);
    }
  
    double getValueLon() {
      return (geoLon);
    }
  
  }

} 
    