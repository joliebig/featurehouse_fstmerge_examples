
package genj.gedcom;

import genj.util.DirectAccessTokenizer;
import genj.util.ReferenceSet;
import genj.util.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;


public class PropertyPlace extends PropertyChoiceValue {

  private final static boolean USE_SPACES = Options.getInstance().isUseSpacedPlaces;

  public final static ImageIcon
    IMAGE = Grammar.V55.getMeta(new TagPath("INDI:BIRT:PLAC")).getImage();

  public final static String
    JURISDICTION_SEPARATOR = ",";

  private final static String
    JURISDICTION_RESOURCE_PREFIX = "prop.plac.jurisdiction.";

  public final static String
    TAG = "PLAC",
    FORM = "FORM";
  
  
  public PropertyPlace(String tag) {
    super(tag);
  }

  
  protected String trim(String value) {

    

    
    StringBuffer buf = new StringBuffer(value.length());
    DirectAccessTokenizer jurisdictions = new DirectAccessTokenizer(value, JURISDICTION_SEPARATOR);
    for (int i=0; ; i++) {
      String jurisdiction = jurisdictions.get(i, true);
      if (jurisdiction==null) break;
      if (i>0) {
        buf.append(JURISDICTION_SEPARATOR);
        if (USE_SPACES) buf.append(' ');
      }
      buf.append(jurisdiction);
    }
    return buf.toString().intern();
  }

  
  protected boolean remember( String theOld, String theNew) {

    
    if (!super.remember(theOld, theNew))
      return false;
    Gedcom gedcom = getGedcom();

    
    DirectAccessTokenizer jurisdictions = new DirectAccessTokenizer(theOld, JURISDICTION_SEPARATOR);
    for (int i=0;;i++) {
      String jurisdiction = jurisdictions.get(i, true);
      if (jurisdiction==null) break;
      
      if (jurisdiction.length()>0)
        gedcom.getReferenceSet(TAG+"."+i).remove(jurisdiction, this);
      
    }

    
    jurisdictions = new DirectAccessTokenizer(theNew, JURISDICTION_SEPARATOR);
    for (int i=0;;i++) {
      String jurisdiction = jurisdictions.get(i, true);
      if (jurisdiction==null) break;
      
      if (jurisdiction.length()>0)
        gedcom.getReferenceSet(TAG+"."+i).add(jurisdiction.intern(), this);
      
    }

    
    return true;
  }

  
  public String[] getFormat() {
    return toJurisdictions(getFormatAsString());
  }
  
  
  public static String[] getFormat(Gedcom gedcom) {
    return toJurisdictions(gedcom.getPlaceFormat());
  }


  private static String[] toJurisdictions(String value) {
    ArrayList<String> result = new ArrayList<String>(10);
    String lastToken = JURISDICTION_SEPARATOR;
    for (StringTokenizer tokens = new StringTokenizer( value, ",", true); tokens.hasMoreTokens(); ) {
      String token = tokens.nextToken();
      if (!JURISDICTION_SEPARATOR.equals(token))
        result.add(token);
      else if (JURISDICTION_SEPARATOR.equals(lastToken))
        result.add("");
      lastToken = token;
    }
    if (JURISDICTION_SEPARATOR.equals(lastToken))
      result.add("");
    return (String[])result.toArray(new String[result.size()]);
  }  
  
  
  public String getFormatAsString() {
    
    String result = "";
    Property pformat = getProperty(FORM);
    if (pformat!=null)
      result = pformat.getValue();
    else {
      Gedcom ged = getGedcom();
      if (ged!=null)
        result = ged.getPlaceFormat();
    }
    
    return result;
  }

  
  public void setFormatAsString(boolean global, String format) {
    if (!global)
      throw new IllegalArgumentException("non-global n/a");
    
    getGedcom().setPlaceFormat(format);
    
    propagatePropertyChanged(this, getValue());
  }

  
  public PropertyPlace[] getSameChoices(int hierarchyLevel) {
    String jurisdiction = getJurisdiction(hierarchyLevel);
    if (jurisdiction==null)
      return null;
    Collection<Property> places = getGedcom().getReferenceSet(TAG+"."+hierarchyLevel).getReferences(jurisdiction);
    return (PropertyPlace[])places.toArray(new PropertyPlace[places.size()]);
  }

  
  public String[] getAllJurisdictions(int hierarchyLevel, boolean sort) {
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return new String[0];
    return getAllJurisdictions(gedcom, hierarchyLevel, sort);
  }

  
  public static String[] getAllJurisdictions(Gedcom gedcom, int hierarchyLevel, boolean sort) {
    ReferenceSet<String,Property> refset = gedcom.getReferenceSet( hierarchyLevel<0 ? TAG : TAG+"."+hierarchyLevel);
    Collection<String> jurisdictions = refset.getKeys(sort ? gedcom.getCollator() : null);
    return (String[])jurisdictions.toArray(new String[jurisdictions.size()]);
  }

  
  public String getFirstAvailableJurisdiction() {
    DirectAccessTokenizer jurisdictions = new DirectAccessTokenizer(getValue(), JURISDICTION_SEPARATOR);
    String result = "";
    for (int i=0; result.length()==0 ;i++) {
      result = jurisdictions.get(i, true);
      if (result==null)
        return "";
    }
    return result;
  }

  
  public String getJurisdiction(int hierarchyLevel) {
    return new DirectAccessTokenizer(getValue(), JURISDICTION_SEPARATOR).get(hierarchyLevel, true);
  }
  
  
  public String[] getJurisdictions() {
    return toJurisdictions(getValue());
  }

  
  public String getCity() {
    int cityIndex = getCityIndex();
    if (cityIndex<0)
      return getFirstAvailableJurisdiction();
    String city = new DirectAccessTokenizer(getValue(), JURISDICTION_SEPARATOR).get(cityIndex, true);
    return city!=null ? city : "";
  }

  
  public String getValueStartingWithCity() {
    
    String result = getValue();
    
    int cityIndex = getCityIndex();
    if (cityIndex<=0)
      return result;
    
    return new DirectAccessTokenizer(result, JURISDICTION_SEPARATOR).getSubstring(cityIndex);
  }

  
  private int getCityIndex() {

    
    if (getFormatAsString().length()==0)
      return -1;

    
    Set<String> cityKeys = Options.getInstance().placeHierarchyCityKeys;
    String[] format = getFormat();
    for (int i=0; i<format.length;i++) {
      if (cityKeys.contains(format[i].toLowerCase()))
        return i;
    }

    
    return -1;
  }
  
  
  public String format(String format) {
      
      if (format == null)
       return getFirstAvailableJurisdiction();
      
      String f = format.trim();
      
      if (f.equals(""))
        return getFirstAvailableJurisdiction();
      
      if (f.equals("all"))
         return getDisplayValue();

      
      StringBuffer result = new StringBuffer();
      String[] jurisdictions = getJurisdictions();
      for (int i=0 ; i < f.length(); i++) {
        char c = f.charAt(i);
        if (Character.isDigit(c)) {
          int j = Character.digit(c,10);
          if (j<jurisdictions.length)
            result.append(jurisdictions[j].trim());
        } else {
          result.append(c);
        }
      }
      return result.toString();
  }

} 
