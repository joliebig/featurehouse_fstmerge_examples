
package genj.gedcom;

import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.Delta;


public class PrivacyPolicy {
  
  public static final PrivacyPolicy 
    PUBLIC = new PrivacyPolicy() { public boolean isPrivate(Property prop) { return false; } },
    PRIVATE = new PrivacyPolicy() { public boolean isPrivate(Property prop) { return true; } };
  
  private boolean infoOfDeceasedIsPublic;
  private int yearsInfoIsPrivate;
  private String tagMarkingPrivate;

  
  private PrivacyPolicy() {
  }
  
  
  public PrivacyPolicy(boolean infoOfDeceasedIsPublic, int yearsInfoIsPrivate, String tagMarkingPrivate) {
    this.infoOfDeceasedIsPublic = infoOfDeceasedIsPublic;
    this.yearsInfoIsPrivate = Math.max(yearsInfoIsPrivate, 0);
    this.tagMarkingPrivate = tagMarkingPrivate==null||tagMarkingPrivate.length()==0 ? null : tagMarkingPrivate;
  }

  
  public String getDisplayValue(Property prop) {
    return isPrivate(prop) ? Options.getInstance().maskPrivate : prop.getDisplayValue();
  }
  
  
  public String getDisplayValue(Property prop, String tag) {
    prop = prop.getProperty(tag);
    return prop==null ? "" : getDisplayValue(prop); 
  }
  
  
  public boolean isPrivate(Property prop) {
    
    
    if (infoOfDeceasedIsPublic&&isInfoOfDeceased(prop))
      return false;
    
    
    if (tagMarkingPrivate!=null&&hasTagMarkingPrivate(prop))
      return true;
    
    
    if (yearsInfoIsPrivate>0&&isWithinPrivateYears(prop))
      return true;
    
    
    prop =  prop.getParent();
    return prop!=null ? isPrivate(prop) : false;
  }
  
  
  private boolean isInfoOfDeceased(Property prop) {
    
    Entity e = prop.getEntity();
    if (e instanceof Indi) 
      return ((Indi)e).isDeceased();
    
    
    if (e instanceof Fam) {
      Indi husband = ((Fam)e).getHusband();
      if (husband!=null&&!husband.isDeceased())
        return false;
      Indi wife = ((Fam)e).getWife();
      return wife!=null && wife.isDeceased();
    }
    
    
    return false;
  }
  
  
  private boolean hasTagMarkingPrivate(Property prop) {
    return getPropertyFor(prop, tagMarkingPrivate, Property.class)!=null;
  }
  
  
  private boolean isWithinPrivateYears(Property prop) {
    
    PropertyDate date = (PropertyDate)getPropertyFor(prop, "DATE", PropertyDate.class);
    if (date==null)
      return false;
    
    Delta anniversary = date.getAnniversary();
    return anniversary!=null&&anniversary.getYears()<yearsInfoIsPrivate;
  }
    
  
  private Property getPropertyFor(Property prop, String tag, Class type) {
    
    for (int i=0, j=prop.getNoOfProperties(); i<j; i++) {
      Property child = prop.getProperty(i);
      if (is(child,tag,type))
        return child;
    }
    return null;
  }
  
  private boolean is(Property prop, String tag, Class type) {
    return prop.getTag().equals(tag) && type.isAssignableFrom(prop.getClass()); 
  }
  
} 
