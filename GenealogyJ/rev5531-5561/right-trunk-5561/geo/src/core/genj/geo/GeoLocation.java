
package genj.geo;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyPlace;
import genj.io.Filter;
import genj.util.DirectAccessTokenizer;
import genj.util.WordBuffer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;


public class GeoLocation extends Point implements Feature, Comparable {

  
  private static Map locale2displayCountry2code = new HashMap();
  
  
   final static FeatureSchema SCHEMA = new FeatureSchema();
  
    final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  
  private Coordinate coordinate;

  
  private String city;
  private Country country;
  private List jurisdictions = new ArrayList(3);
  private int hash;
  
  
  protected List properties = new ArrayList();
  
  
  private int matches = 0;
  
  
   GeoLocation(String city, String jurisdiction, Country country) {
    
    super(GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(new Coordinate[]{ new Coordinate() } ), GEOMETRY_FACTORY);
    coordinate = super.getCoordinate();
    
    this.city = city;
    if (jurisdiction!=null) this.jurisdictions.add(jurisdiction);
    this.country = country;
    
  }
  
  
  public GeoLocation(Property prop) {
    super(GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(new Coordinate[]{ new Coordinate() } ), GEOMETRY_FACTORY);
    
    
    coordinate = super.getCoordinate();
    
    
    properties.add(prop);
    
    
    Property plac = prop.getProperty("PLAC");
    Property addr = prop.getProperty("ADDR");
    
    
    if (plac instanceof PropertyPlace) {
       parsePlace( (PropertyPlace)plac );
    } else if (addr!=null) {
        parseAddress(addr);
    } else {
      throw new IllegalArgumentException("can't locate "+prop.getTag()+" "+prop);
    }
    
    
  }
  
  
  public String getJurisdictionsAsString() {
    WordBuffer result = new WordBuffer(", ");
    result.append(city);
    for (int i = 0; i < jurisdictions.size(); i++) 
      result.append(jurisdictions.get(i));
    result.append(country);
    return result.toString();
  }  
  
  
  public String getCoordinateAsString() {
    return getCoordinateAsString(coordinate);
  }
  public static String getCoordinateAsString(Coordinate coord) {
    return getCoordinateAsString(coord.y, coord.x);
  }
  public static String getCoordinateAsString(double lat, double lon) {
    if (Double.isNaN(lat)||Double.isNaN(lon))
      return "n/a";
    char we = 'E', ns = 'N';
    if (lat<0) { lat = -lat; ns='S'; }
    if (lon<0) { lon = -lon; we='W'; }
    DecimalFormat format = new DecimalFormat("0.0");
    return ns + format.format(lat) + " " + we + format.format(lon);
  }
  
  
  public Coordinate getCoordinate() {
    return coordinate;
  }

  
  public static Set parseEntities(Collection entities) {
    return parseEntities(entities, null);
  }
  
  
  public static Set parseEntities(Collection entities, Filter filter) {
    
    
    List props = new ArrayList(100);
    for (Iterator it=entities.iterator(); it.hasNext(); ) {
      Entity entity = (Entity)it.next();
      for (int p=0; p<entity.getNoOfProperties(); p++) {
        Property prop = entity.getProperty(p);
        if ( (prop.getProperty("PLAC")!=null||prop.getProperty("ADDR")!=null) && (filter==null||filter.checkFilter(prop))) 
          props.add(prop);
      }
    }
    
    
    return parseProperties(props);
  }

  
  public static Set parseProperties(Collection properties) {
    
    
    Map result = new HashMap(properties.size());
    List todo = new ArrayList();
    
    
    for (Iterator it = properties.iterator(); it.hasNext(); ) {
      
      Property prop = (Property)it.next();

      
      GeoLocation location;
      try {
        location = new GeoLocation(prop);
      } catch (IllegalArgumentException e) {
        continue;
      }
    
      
      GeoLocation other = (GeoLocation)result.get(location);
      if (other!=null) {
        other.add(location);
        continue;
      }
      result.put(location, location);
    
      
    }
    
    
    return result.keySet();
  }
  
  
  private void parseAddress(Property addr) {
    
    Gedcom ged =addr.getGedcom();
    
    
    
    
    Property pcity = addr.getProperty("CITY");
    if (pcity==null)
      throw new IllegalArgumentException("can't determine city from address");
    parseJurisdictions( pcity.getDisplayValue(), ged, false);
    
    
    Locale locale = addr.getGedcom().getLocale();
    Property pcountry = addr.getProperty("CTRY");
    if (pcountry!=null)  
      country = Country.get(ged.getLocale(), trim(pcountry.getDisplayValue()));
    
    
    Property pstate = addr.getProperty("STAE");
    if (pstate!=null) {
      String state = pstate.getDisplayValue();
      if (state.length()>0) jurisdictions.add(state);
    }
    
    
    return;
  }
  
  
  private void parsePlace(PropertyPlace place) {
    
    parseJurisdictions(place.getValueStartingWithCity(), place.getGedcom(), true);
  }
  
  
  private void parseJurisdictions(String jurisdictions, Gedcom gedcom, boolean lookForCountry) {
    
    DirectAccessTokenizer tokens = new DirectAccessTokenizer(jurisdictions, PropertyPlace.JURISDICTION_SEPARATOR);
    int first = 0, last = tokens.count()-1;
    
    
    while (true) {
      city = trim(tokens.get(first++));
      if (city==null)
        throw new IllegalArgumentException("can't determine jurisdiction's city");
      if (city.length()>0) 
        break;
    }
    
    
    if (lookForCountry) {
      Locale locale = gedcom.getLocale();
      if (last>=first) {
        country = Country.get(gedcom.getLocale(), trim(tokens.get(last)));
        if (country!=null) 
          last--;
      }
    }
    
    
    for (int i=first; i<=last; i++) {
      String jurisdiction = trim(tokens.get(i));
      if (jurisdiction.length()>0) this.jurisdictions.add(jurisdiction);
    }
    
    
  }
  
  
  private String trim(String jurisdiction) {
    if (jurisdiction==null)
      return null;
    for (int i=0, j=jurisdiction.length(); i<j ;i++) {
      char c = jurisdiction.charAt(i); 
      if (c=='(' || c=='[' || c=='/' || c=='\\')
         return jurisdiction.substring(0, i).trim();
    }
    return jurisdiction.trim();
  }
  
  
  public void add(GeoLocation other) {
    for (Iterator it = other.properties.iterator(); it.hasNext(); ) {
      Object prop = it.next();
      if (!properties.contains(prop))
        properties.add(prop);
    }
    Collections.sort(properties, new PropertyComparator(".:DATE"));
  }
  
  
  public void removeEntities(Set entities) {
    for (ListIterator it = properties.listIterator(); it.hasNext(); ) {
      Property prop = (Property)it.next();
      if (entities.contains(prop.getEntity()))
        it.remove();
    }
  }
  
  
  public void removeEntity(Entity entity) {
    for (ListIterator it = properties.listIterator(); it.hasNext(); ) {
      Property prop = (Property)it.next();
      if (entity == prop.getEntity())
        it.remove();
    }
  }
  
  
  public int getMatches() {
    return matches;
  }
  
  
  public boolean isValid() {
    return matches>0 && !Double.isNaN(coordinate.x) && !Double.isNaN(coordinate.y);
  }
  
  
  public Gedcom getGedcom() {
    return ((Property)properties.get(0)).getGedcom();
  }

  
  public int hashCode() {
    if (hash==0) {
      
      if (city!=null) hash += city.toLowerCase().hashCode();
      for (int i=0;i<jurisdictions.size();i++)
          hash += jurisdictions.get(i).toString().toLowerCase().hashCode();
      if (country!=null) hash += country.getCode().toLowerCase().hashCode();
    }
    return hash;
  }

  
  public boolean equals(Object obj) {
    GeoLocation that = (GeoLocation)obj;
    return equals(this.city, that.city) && this.jurisdictions.equals(that.jurisdictions) && equals(this.country, that.country);
  }
  
  private static boolean equals(Object o1, Object o2) {
    if (o1==null&&o2==null)
      return true;
    if (o1==null||o2==null)
      return false;
    return o1.equals(o2);
  }

  
  public String getCity() {
    return city;
  }

  
  public List getJurisdictions() {
    return jurisdictions;
  }
  
  GeoLocation addJurisdiction(String j) {
    jurisdictions.add(j);
    return this;
  }

  
  public Country  getCountry() {
    return country;
  }
  
  
  public int getNumProperties() {
    return properties.size();
  }
  
  public Property getProperty(int i) {
    return (Property)properties.get(i);
  }
  
  public int getPropertyIndex(Property prop) {
    return properties.indexOf(prop);
  }
  
  
  protected void setCoordinate(Coordinate coord) {
    setCoordinate(coord.y, coord.x);
  }
  
  
  protected void setCoordinate(double lat, double lon) {
    coordinate.x = lon;
    coordinate.y = lat;
    matches = 1;
  }
  
  
  protected void setMatches(int set) {
    matches = set;
  }
  
  
  public String toString() {
    return getJurisdictionsAsString();
  }
  
  
  public void setAttributes(Object[] arg0) {
    throw new IllegalArgumentException();
  }

  
  public void setSchema(FeatureSchema arg0) {
    throw new IllegalArgumentException();
  }

  
  public int getID() {
    throw new IllegalArgumentException();
  }

  
  public void setAttribute(int arg0, Object arg1) {
    throw new IllegalArgumentException();
  }

  
  public void setAttribute(String arg0, Object arg1) {
    throw new IllegalArgumentException();
  }

  
  public void setGeometry(Geometry arg0) {
    throw new IllegalArgumentException();
  }
  
  
  public Object getAttribute(int arg0) {
    throw new IllegalArgumentException();
  }

  
  public Object getAttribute(String arg0) {
    return city;
  }

  
  public String getString(int arg0) {
    throw new IllegalArgumentException();
  }

  
  public int getInteger(int arg0) {
    throw new IllegalArgumentException();
  }

  
  public double getDouble(int arg0) {
    throw new IllegalArgumentException();
  }

  
  public String getString(String arg0) {
    throw new IllegalArgumentException();
  }

  
  public Geometry getGeometry() {
    return this;
  }

  
  public FeatureSchema getSchema() {
    return SCHEMA;
  }

  
  public Object clone() {
    throw new IllegalArgumentException();
  }

  
  public Feature clone(boolean arg0) {
    throw new IllegalArgumentException();
  }

  
  public Object[] getAttributes() {
    throw new IllegalArgumentException();
  }

  
  public int compareTo(Object o) {
    GeoLocation that = (GeoLocation)o;
    return this.city.compareToIgnoreCase(that.city);
  }
  
}
