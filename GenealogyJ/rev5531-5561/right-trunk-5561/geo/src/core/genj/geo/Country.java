
package genj.geo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;


public class Country implements Comparable {
  
  public final static Country HERE = Country.get(Locale.getDefault().getCountry());
  
  private static Country[] ALL_COUNTRIES = null;
  
  private static Country DEFAULT_COUNTRY = null;
  
  private final static Map locale2countries = new HashMap();
  
  private final static Map displayName2Country = new WeakHashMap();
  
  
  private String iso;
  private String fips;
  private String displayName;
  
  
  private Country(String code) {
    iso = code.toLowerCase();
    displayName =  new Locale(Locale.getDefault().getLanguage(), code).getDisplayCountry();
  }
  
  
  private Country(String code, String displayName) {
    iso = code.toLowerCase();
    this.displayName = displayName;
  }
  
  
  public String getDisplayName() {
    return displayName;
  }
  
  
  public String toString() {
    return displayName;
  }
  
  
  public String getCode() {
    return iso;
  }
  
  
  public int hashCode() {
    return iso.hashCode();
  }
  
  
  public int compareTo(Object o) {
    return toString().compareTo(o.toString());
  }
  
  
  public boolean equals(Object obj) {
    if (obj==null) return false;
    Country that = (Country)obj;
    return iso.equals(that.iso);
  }
  
  
  public static Country getDefaultCountry() {
    getAllCountries();
    return DEFAULT_COUNTRY;
  }
  
  
  public static Country[] getAllCountries() {
    
    
    if (ALL_COUNTRIES==null) {
    
      
      String[] codes = Locale.getISOCountries(); 
      ALL_COUNTRIES = new Country[codes.length];
      for (int i=0;i<codes.length;i++) 
        ALL_COUNTRIES[i] = new Country(codes[i]);
    
      Arrays.sort(ALL_COUNTRIES);
      
      
      DEFAULT_COUNTRY = get(Locale.getDefault().getCountry());
      
    }    
    
    
    return ALL_COUNTRIES;
  }
  
  
  public static Country get(String code) {
    return new Country(code);
  }
  
  
  public static Country get(Locale locale, String displayName) {
    
    
    if (displayName2Country.containsKey(displayName))
      return (Country)displayName2Country.get(displayName);
    
    
    List countries = (List)locale2countries.get(locale);
    if (countries==null) {
      
      String[] codes = Locale.getISOCountries(); 
      countries = new ArrayList(codes.length);
      for (int i = 0; i < codes.length; i++) 
        countries.add(new Country(codes[i], new Locale(locale.getLanguage(), codes[i]).getDisplayCountry(locale)));
      
      locale2countries.put(locale, countries);
    }
    
    
    Country result  = null;
    
    Collator collator = Collator.getInstance(locale);
    collator.setStrength(Collator.PRIMARY);
    for (int i = 0; i < countries.size(); i++) {
      Country country = (Country)countries.get(i);
      









      if (collator.compare(country.getDisplayName(), displayName)==0) {
        
        result = new Country(country.iso, displayName);
        break;
      }
    }
    
    
    if (result==null&&!locale.getLanguage().equals(Locale.ENGLISH.getLanguage()))
        result = get(Locale.ENGLISH, displayName);

    
    displayName2Country.put(displayName, result);
    
    
    return result;
  }

} 