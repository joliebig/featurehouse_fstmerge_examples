
package geo;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
import genj.geo.GeoLocation;
import genj.geo.GeoService;
import genj.geo.GeoServiceException;
import genj.io.FileAssociation;
import genj.io.Filter;
import genj.report.Report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;


public class ReportGoogleMap extends Report {

  private final static DecimalFormat FORMAT = new DecimalFormat("##0.###", new DecimalFormatSymbols(Locale.US));
  
  public int maxEventsPerLocation = 3;

  
  public String yearFilter = "";
  
  
  public int mapHeight = 400;

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Gedcom ged) {
    operate(ged, ged.getEntities());
  }

  
  public void start(Indi indi) {
    operate(indi.getGedcom(), Collections.singletonList(indi));
  }

  
  public void start(Indi[] indis) {
    operate(indis[0].getGedcom(), Arrays.asList(indis));
  }

  
  private void operate(Gedcom ged, Collection indis) {

    
    Collection locations = GeoLocation.parseEntities(indis, yearFilter.length()==0 ? null : new YearFilter());
    
    
    try {
      locations = GeoService.getInstance().match(ged, locations, true);
    } catch (GeoServiceException e) {
      super.println(e.getMessage());
      locations.clear();
    }
    if (locations.isEmpty()) {
      getOptionFromUser(translate("none_mapable"), OPTION_OK);
      return;
    }

    
    String key = getValueFromUser("google-key", translate("enter_key"));
    if (key==null)
      return;
    
    
    File html = getFileFromUser(translate("which_html_file"), translate("generate"));
    if (html==null)
      return;
    String suffix = FileAssociation.getSuffix(html);
    if (!suffix.toLowerCase().startsWith("htm")) {
      suffix = "html";
      html = new File(html.getAbsolutePath()+"."+suffix);
    }
    File xml = new File(html.getAbsolutePath().replaceAll("."+suffix, ".xml"));
    File kml = new File(html.getAbsolutePath().replaceAll("."+suffix, ".kml"));

    
    if (!writeHTML(ged, (GeoLocation)locations.iterator().next(), html, xml, key))
      return;

    
    if (!writeXML(locations, xml, kml))
      return;

    
    getOptionFromUser(translate("done", new String[] { html.getName(), xml.getName(), html.getParent() }), OPTION_OK );

    
  }
  
  
  private boolean writeHTML(Gedcom ged, GeoLocation center, File html, File xml, String key) {

    String[] match = { "MAPGED", "MAPLAT", "MAPLON", "MAPXML", "MAPKEY", "MAPHEIGHT" };
    String[] replace = { ged.getName(), FORMAT.format(center.getY()), FORMAT.format(center.getX()), xml.getName(), key, Integer.toString(Math.max(128, mapHeight)) };

    
    try {
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(html), Charset.forName("UTF8")));
      BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("gmap-template.html"), Charset.forName("UTF8")));
      while (true) {
        
        String line = in.readLine();
        if (line==null) break;
        
        for (int m=0;m<match.length;m++)  {
          int i = line.indexOf(match[m]);
          if (i>=0) {
            line = line.substring(0,i) + replace[m] + line.substring(i+match[m].length());
            break;
          }
        }
        
        out.write(line);
        out.write("\n");
      }
      in.close();
      out.close();
    } catch (IOException e) {
      getOptionFromUser(translate("ioerror", html), OPTION_OK);
      return false;
    }
    
    return true;
  }

  
  private boolean writeXML(Collection locations, File xml, File kml) {

    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    try {
      BufferedWriter xmlout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xml), Charset.forName("UTF8")));
      xmlout.write("<ls>");
      
      BufferedWriter kmlout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kml), Charset.forName("UTF8")));
      kmlout.write("<kml xmlns=\"http://earth.google.com/kml/2.2\">");
      kmlout.write("<Folder>");
      
      for (Iterator it=locations.iterator(); it.hasNext(); ) {
        
        
        GeoLocation location = (GeoLocation)it.next();
        if (!location.isValid()) 
          continue;
    
        writeXML(location, xmlout);
        writeKML(location, kmlout);
        
        
      }
      
      xmlout.write("</ls>");
      xmlout.close();
      
      kmlout.write("</Folder>");
      kmlout.write("</kml>");
      kmlout.close();
      
    } catch (IOException e) {
      getOptionFromUser(translate("ioerror", xml), OPTION_OK);
      return false;
    }

    
    return true;
  }
  
  private void writeKML(GeoLocation location, BufferedWriter out) throws IOException {
 
    out.write("<Placemark>");
    out.write("<name>");
    out.write(location.toString());
    out.write("</name>");
    out.write("<description>");
    writeProperties(location, out, "\n");
    out.write("</description>");
    out.write("<Point>");
    out.write("<coordinates>");
    out.write(FORMAT.format(location.getX()));
    out.write(",");
    out.write(FORMAT.format(location.getY()));
    out.write("</coordinates>");
    out.write("</Point>");
    out.write("</Placemark>");
  }
  
  private void writeXML(GeoLocation location, BufferedWriter out) throws IOException {
    
    
    out.write("<l x=\"" + FORMAT.format(location.getX()) + "\" y=\"" + FORMAT.format(location.getY()) + "\">");
    out.write(location.toString());

    
    writeProperties(location, out, ";");
    
    
    out.write("</l>");
  }
  
  private void writeProperties(GeoLocation location, BufferedWriter out, String delim) throws IOException {
    
    int max = maxEventsPerLocation<=0 ? Integer.MAX_VALUE : maxEventsPerLocation;
    for (int p=0;p<max&&p<location.getNumProperties();p++) {
      out.write(delim);
      Property prop = location.getProperty(p);
      Property date = prop.getProperty("DATE", true);
      if (date!=null) {
        out.write(date.toString());
        out.write(" ");
      }
      out.write(Gedcom.getName(prop.getTag()));
      out.write(" ");
      out.write(prop.getEntity().toString());
    }
    
  }

  
  private class YearFilter implements Filter {
    
    int 
      lower = Integer.MIN_VALUE, 
      upper = Integer.MAX_VALUE;
  
    private YearFilter() {
    
      if (yearFilter.length()==0)
        return;
      
      
      StringBuffer buf = new StringBuffer();
      while (!Character.isDigit(yearFilter.charAt(buf.length())))
        buf.append(yearFilter.charAt(buf.length()));
      String op = buf.toString().trim();

      
      int year;
      try {
        year = Integer.parseInt(yearFilter.substring(op.length()).trim());
      } catch (NumberFormatException e) {
        println("Can't parse year in filter "+yearFilter);
        return;
      }

      
      if (op.equals("")||op.equals("=")) {
        lower = year;
        upper = year;
        return;
      }
      if (op.equals("<")) {
        upper = year-1;
        return;
      }
      if (op.equals("<=")) {
        upper = year;
        return;
      }
      if (op.equals(">")) {
        lower = year+1;
        return;
      }
      if (op.equals(">=")) {
        lower = year;
        return;
      }
      
      println("Can't find operator in filter "+yearFilter);
      
    }
    
    public String getFilterName() {
      return "Filter by Years";
    }
    
    public boolean checkFilter(Property property) {
      
      
      Property date = property.getProperty("DATE"); 
      if (date instanceof PropertyDate&&date.isValid()&&!isIn((PropertyDate)date))
          return false;

      
      Entity ent = property.getEntity();
      if ( (ent instanceof Indi) && isIn(((Indi)ent).getBirthDate()))
        return true;
      
      
      return false;
    }
    
    private boolean isIn(PropertyDate date) {
      if (date==null)
        return false;
      PointInTime start = date.getStart(); 
      if (start.getYear()>upper)
        return false;
      PointInTime end = date.isRange() ? date.getEnd() : start; 
      if (end.getYear()<lower)
        return false;
      return true;
    }
    
  } 
  
} 
