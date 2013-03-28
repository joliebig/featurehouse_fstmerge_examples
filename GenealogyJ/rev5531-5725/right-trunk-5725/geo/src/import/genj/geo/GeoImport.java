
package genj.geo;

import genj.util.DirectAccessTokenizer;
import genj.util.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;


public class GeoImport {

  private static Charset UTF8 = Charset.forName("UTF8");
  private static Parser USGS = new USGSParser(), GNS = new GNSParser(); 
  
  private Writer sqlOut;
  private int nLocations,nJurisdictions;
  
  
  private GeoImport(File out) throws IOException {
    this.sqlOut = new OutputStreamWriter(new FileOutputStream(out), UTF8);
    sqlOut.write("SET NAMES utf8; SET CHARACTER SET utf8;");
  }
  
  
  private static void log(String msg) {
    System.out.println(msg);
  }

  
  public static void main(String[] args) {
    
    
    if (args.length<2) {
      log("Use : GeoImport [-j create jurisdictions] [path to folder with geodata files from USGS or GNS] [output filename]");
      return;
    }
    
    
    try {
      
      int files = 0;
      
      
      boolean jurisdictions = false;
      if (args[0].equals("-j")) {
        jurisdictions = true;
        files++;
      }
      
      
      GeoImport gi = new GeoImport(new File(args[files+1]));
      if (jurisdictions) gi.parseJurisdictions();
      gi.parseFolder(new File(args[files]));
      gi.close();
      
      
      log("Done: "+gi.nLocations+" places and "+gi.nJurisdictions+" jurisdictions generated");
      
    } catch (IOException e) {
      log("Err : "+e.getMessage());
    }

  }
  
  
  private void parseJurisdictions() throws IOException {
    
    
    Resources jurisdictions = new Resources(getClass().getResourceAsStream("jurisdictions.properties"));
    
      
      
      
      
      
      for (Iterator keys = jurisdictions.getKeys().iterator(); keys.hasNext(); ) {
        
        String key = keys.next().toString();
        if (key.length()!=5) continue;
        
        String country = key.substring(0,2);
        String adm1 = key.substring(3,5);
        
        StringTokenizer names = new StringTokenizer(jurisdictions.getString(key), ",");
        for (int n=0; names.hasMoreTokens(); n++) {
          nJurisdictions++;
          
          sqlOut.write("INSERT INTO jurisdictions VALUES (\"");
          sqlOut.write(country.toLowerCase());
          sqlOut.write("\",\"");
          sqlOut.write(adm1.toLowerCase()); 
          sqlOut.write("\",\"");
          sqlOut.write(names.nextToken().trim());
          sqlOut.write("\",");
          sqlOut.write(n==0 ? '1' : '0');
          sqlOut.write(");");
          
        }
      }
      
      

  }
  
  
  private void parseFolder(File folder) throws IOException {
    
    
    File[] files = folder.listFiles(); 
    for (int f=0;f<files.length;f++) { 
      
      
      File file = files[f];
      if (file.isFile())
        parseFile(files[f]);

      
    }
    
     
  }
  
  
  private void  parseFile(File file) throws IOException {
    
    
    Parser parser = getParser(file);
    if (parser==null) {
      log("Info: Skipping "+file);
      return;
    }
    log("Info: Parsing "+file+" as "+parser.getName());
    
    
    String filename = file.getName();
    if (filename.indexOf('.')>0) filename = filename.substring(0, filename.indexOf('.'));
    
    
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF8));

    in.readLine(); 
    
    while (true) {
      
      String line = in.readLine();
      if (line==null) break;
      
      if (parser.parse(line, filename))  {
        nLocations++;
        parser.write(sqlOut);
      }
      
    }
    
    in.close();
    
    
  }
  
  
  private void close() throws IOException {
    if (sqlOut!=null) {
      sqlOut.close();
      sqlOut = null;
    }      
  }

  
  private Parser getParser(File file) {
    
    String name = file.getName();
    
    if (name.matches("[A-Z][A-Z]_DECI.TXT"))
      return USGS;
    
    if (name.matches("[a-z][a-z].txt"))
      return GNS;
    
    return null;
  }
  
  
  private static abstract class Parser {
    
    String country;
    String state;
    String city;
    float lat, lon;
    
    abstract String getName();
    abstract boolean parse(String line, String filename);
    
    
    void write(Writer out) throws IOException {

      
      if (city.indexOf('\"')>0) {
        log("Warn: removing quote from city "+city);
        city = city.replace('\"', ' ');
      }
      if (country.indexOf('\"')>0) 
        throw new IOException("Found quote in text"+country);
      if (country.length()!=2) 
        throw new IOException("Found bad country "+country);
      if (state.indexOf('\"')>0) 
        throw new IOException("Found quote in text"+state);
      if (state.length()>2) 
        throw new IOException("Found bad state "+state);

      
      out.write("INSERT INTO locations VALUES (\"");
      out.write(city);
      out.write("\",\"");
      out.write(state.toLowerCase()); 
      out.write("\",\"");
      out.write(country.toLowerCase());
      out.write("\",");
      out.write(Float.toString(lat));
      out.write(",");
      out.write(Float.toString(lon));
      out.write(");");
      
      
    }
    
  }
  
  
  private static class USGSParser extends Parser {
    
    
    String getName() { return "USGS"; }
    
    
    boolean parse(String line, String filename) {
      
      
      DirectAccessTokenizer values = new DirectAccessTokenizer(line, "|");

      
      country = "us";
      
      
      state = values.get(1);
      
      
      city = values.get(2);
      
      
      if (!"ppl".equals(values.get(3))) 
        return false;
      
      
      try {
        String 
          sLat = values.get(9),
          sLon = values.get(10); 
        if (sLat.length()==0||"UNKNOWN".equals(sLat)||sLon.length()==0||"UNKNOWN".equals(sLon)) 
          return false;
        lat = Float.parseFloat(sLat); 
        lon = Float.parseFloat(sLon); 

      } catch (NumberFormatException e) {
        log("Info: format problem in: "+line);
        return false;
      }
      
      
      return true;

    }
  }
  
  
  private static class GNSParser extends Parser  {

    private static Properties fips2iso;

    
    GNSParser() {
      fips2iso = new Properties();
      try {
        fips2iso.load(GeoImport.class.getResourceAsStream("fips2iso.properties"));
      } catch (IOException e) {
      }
    }
    
    
    String getName() { return "GNS"; }

    
    boolean parse(String line, String filename) {
      
      
      DirectAccessTokenizer values = new DirectAccessTokenizer(line, "\t");
      
      try {
        lat = Float.parseFloat(values.get(3)); 
        lon = Float.parseFloat(values.get(4)); 
      } catch (NumberFormatException e) {
        log("Info: format problem in: "+line);
        return false;
      }

      
      String cat = values.get(9);
      if (cat.length()==0||'P'!=cat.charAt(0)) 
        return false;
      
      
      country = filename.toLowerCase();
      String iso = (String)fips2iso.get(country);
      if (iso!=null) country = iso;
      
      
      state = values.get(13);
      if (state.length()>2) 
        return false;
      
      
      city = values.get(22); 

      
      return true;
      
    }
    
  }
  
}
