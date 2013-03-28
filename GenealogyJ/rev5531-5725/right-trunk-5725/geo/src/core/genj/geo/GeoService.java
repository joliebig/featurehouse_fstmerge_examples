
package genj.geo;

import genj.Version;
import genj.gedcom.Gedcom;
import genj.util.DirectAccessTokenizer;
import genj.util.EnvironmentChecker;
import genj.util.Registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;



public class GeoService {
  
  final static Integer TIMEOUT = new Integer(10*1000);
  final static Charset UTF8 = Charset.forName("UTF8");
  final static Logger LOG = Logger.getLogger("genj.geo");
  final static URL URL = createQueryURL();
  
  
  private static final String GEO_DIR = "geo";

  
  private static GeoService instance;
  
  
  private List maps;
  
  
  private static URL createQueryURL() {
    try {
      return new URL("http://genj.sourceforge.net/php/geoq.php"); 
    } catch (MalformedURLException e) {
      throw new Error("init");
    }
  }
  
  
  private GeoService() {
   }
  
  
  public static GeoService getInstance() {
    if (instance==null) {
      synchronized (GeoService.class) {
        if (instance==null) 
          instance = new GeoService();
      }
    }
    return instance;
  }
  
  
   File[] getGeoFiles() {
    
    List result = new ArrayList();
    
    String[] dirs  = {
        EnvironmentChecker.getProperty(this, "user.home.genj/geo", "", "looking for user's geo files"),
        EnvironmentChecker.getProperty(this, "all.home.genj/geo", "", "looking for shared geo files"),
        EnvironmentChecker.getProperty(this, "genj.geo.dir", GEO_DIR, "looking for installed geo files")
    };
    
    
    for (int i=0;i<dirs.length;i++) {
      File dir = new File(dirs[i]);
      if (dir.isDirectory()) {
        result.addAll(Arrays.asList(dir.listFiles()));
      }
    }

    
    return (File[])result.toArray(new File[result.size()]);
  }
  
  
  private Registry getRegistry(Gedcom gedcom) {
    String name = gedcom.getName();
    if (name.endsWith(".ged")) name = name.substring(0, name.length()-".ged".length());
    name = name + ".geo";
    return Registry.lookup(name, gedcom.getOrigin());
  }
  
  
  private String encode(GeoLocation location) {
    
    
    StringBuffer query = new StringBuffer();
    query.append(location.getCity());
    query.append(",");
    if (location.getJurisdictions().isEmpty()) {
      query.append(",");
    } else for (Iterator it = location.getJurisdictions().iterator(); it.hasNext(); ) {
      query.append(it.next().toString());
      query.append(",");
    }
    Country c = location.getCountry();
    if (c!=null) query.append(c.getCode());

    
    return query.toString();
  }

  
  private GeoLocation decode(String location) {
    
    
    DirectAccessTokenizer tokens = new DirectAccessTokenizer(location, ",", false);
    if (tokens.count()!=5)
      return null;
    
    GeoLocation result = new GeoLocation(tokens.get(0), tokens.get(1), Country.get(tokens.get(2))); 
    
    try {
      result.setCoordinate(Float.parseFloat(tokens.get(3)), Float.parseFloat(tokens.get(4)));
    } catch (NumberFormatException e) {
      return null;
    }
    
    return result;
  }
  
  private final static String HEADER = "GEOQ:"
    +System.getProperty("user.name")+","
    +System.getProperty("os.name")+","
    +"GenJ "+Version.getInstance().getBuildString();
  
  
  protected List webservice(URL url, List locations, boolean followRedirect) throws GeoServiceException {

    long start = System.currentTimeMillis();
    int rowCount = 0, hitCount = 0;
    try {
      
      
      HttpURLConnection con;
      try {
        con = (HttpURLConnection)url.openConnection();
        
        try {
          con.getClass().getMethod("setConnectTimeout", new Class[]{ Integer.TYPE }).invoke(con, new Object[]{ TIMEOUT } );
        } catch (Throwable t) {
          LOG.info("can't set connection timeout");
        }
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
  
        
        Writer out = new OutputStreamWriter(con.getOutputStream(), UTF8);
        out.write(HEADER+"\n");
        for (int i=0;i<locations.size();i++) {
          if (i>0) out.write("\n");
          out.write(encode((GeoLocation)locations.get(i)));
        }
        out.close();
      } catch (IOException e) {
        throw new GeoServiceException("Accessing GEO Webservice failed");
      }
      
      
      List rows  = new ArrayList();
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF8));
        for (int l=0;l<locations.size();l++) {
          
          
          String line = in.readLine();
          if (line==null) break;
          
          
          if (l==0&&followRedirect) {
            try {
              return webservice(new URL(line), locations, false);
            } catch (MalformedURLException e) {
            }
          }
          
          
          rowCount++;
          List row = new ArrayList();
          if (!line.startsWith("?")) {
            StringTokenizer hits = new StringTokenizer(line, ";");
            while (hits.hasMoreTokens()) {
              GeoLocation hit = decode(hits.nextToken());
              if (hit!=null) {
                row.add(hit);
                hitCount++;
              }
            }
          }
          rows.add(row);
        }
        in.close();
      } catch (IOException e) {
        throw new GeoServiceException("Reading from GEO Webservice failed");
      }

      
      if (rows.size()<locations.size()) 
        throw new GeoServiceException("GEO Webservice returned "+rows.size()+" rows for "+locations.size()+" locations");
      
      
      return rows;
      
    } finally {
      long secs  = (System.currentTimeMillis()-start)/1000;
      LOG.info("query for "+locations.size()+" locations in "+secs+"s resulted in "+rowCount+" rows and "+hitCount+" total hits");
    }
    
  }

  
  public List query(GeoLocation location) throws GeoServiceException {
    
    List rows = webservice(URL, Collections.singletonList(location ), true);
    return rows.isEmpty() ?  new ArrayList() : (List)rows.get(0);
  }
  
  
  public Collection match(Gedcom gedcom, Collection locations, boolean matchAll) throws GeoServiceException {

    
    Registry registry = gedcom!=null ? getRegistry(gedcom) : new Registry();
    
    
    List matched = new ArrayList(locations.size());
    List todos = new ArrayList(locations.size());
    for (Iterator it=locations.iterator(); it.hasNext(); ) {
      GeoLocation location = (GeoLocation)it.next();
      
      String restored  = registry.get(location.getJurisdictionsAsString(), (String)null);
      if (restored!=null) try {
        StringTokenizer tokens = new StringTokenizer(restored, ",");
        location.setCoordinate( Double.parseDouble(tokens.nextToken()), Double.parseDouble(tokens.nextToken()));
        if (tokens.hasMoreTokens())
          location.setMatches(Integer.parseInt(tokens.nextToken()));
      } catch (Throwable t) {
      }
      
      if (location.isValid())
        matched.add(location);
      else
        todos.add(location);
    }
    
    
    if (todos.isEmpty() || (todos.size()!=locations.size()&&!matchAll) )
      return matched;
    
    
    List rows = webservice(URL, todos, true);
    
    
    for (int i=0; i<todos.size(); i++) {
      GeoLocation todo  = (GeoLocation)todos.get(i);
      List hits = (List)rows.get(i);
      
      if (!hits.isEmpty()) {
        
        GeoLocation match = null;
        int matchScore = -1;
        for (int h=0;h<hits.size();h++) {
          GeoLocation hit = (GeoLocation)hits.get(h);
          int hitScore = 0;
          if (todo.getCity().equals(hit.getCity())) hitScore+=8;
          if (todo.getJurisdictions().containsAll(hit.getJurisdictions())) hitScore+=4;
          if (todo.getCountry()!=null&&todo.getCountry().equals(hit.getCountry())) hitScore+=2;
          if (Country.HERE.equals(hit.getCountry())) hitScore+=1;
          if (hitScore>matchScore) {
            match = hit;
            matchScore = hitScore;
          }
        }
        
        todo.setCoordinate(match.getCoordinate());
        todo.setMatches(hits.size());
        remember(gedcom, todo);
        matched.add(todo);
      }
    }
    
    
    return matched;
  }

  
  public void remember(Gedcom gedcom, GeoLocation location) {
    if (gedcom==null)
      return;
    Coordinate coord = location.getCoordinate();
    getRegistry(gedcom).put(location.getJurisdictionsAsString(), coord.y + "," + coord.x + "," + location.getMatches());
  }
  
  
  public synchronized GeoMap[] getMaps() {
    
    
    if (maps==null) {
      
      maps = new ArrayList();
      
      
      File[] files = getGeoFiles();
      for (int i=0;i<files.length;i++) {
        
        if (files[i].getName().equals("CVS"))
          continue;
        
        if (files[i].isDirectory()||files[i].getName().endsWith(".zip")) try {
          maps.add(new GeoMap(files[i]));
        } catch (Throwable t) {
          LOG.log(Level.SEVERE, "problem reading map from "+files[i], t);
        }
        
      }
      
      
    }
    
    
    return (GeoMap[])maps.toArray(new GeoMap[maps.size()]);
  }
    
} 
