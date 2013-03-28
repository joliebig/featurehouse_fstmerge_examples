
package genj.gedcom;

import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;

import java.text.Collator;

import javax.swing.ImageIcon;


public class PropertyAge extends Property {

  public final static String TAG = "AGE";
  public final static ImageIcon IMG = Grammar.V55.getMeta(new TagPath("INDI:BIRT:AGE")).getImage();
  
  
  private Delta age = new Delta(0, 0, 0);
  private int younger_exactly_older = 0;

  
  private String ageAsString;
  
  public static String[] PHRASES = {
    "CHILD", "INFANT", "STILLBORN"
  };

  
  public boolean isValid() {
    
    Collator c = getGedcom().getCollator();
    
    if (ageAsString == null)
      return true;
    for (int i = 0; i < PHRASES.length; i++) 
      if (c.compare(PHRASES[i], ageAsString)==0)
        return true;
    
    return false;
  }

  
   void afterAddNotify() {
    
    super.afterAddNotify();
    
    updateAge();
    
  }

  
  public String getTag() {
    return TAG;
  }
  
  
  public static String getLabelForAge() {
    return Gedcom.getName(TAG);
  }

  
  Property init(MetaProperty meta, String value) throws GedcomException {
    meta.assertTag(TAG);
    return super.init(meta, value);
  }

  
  public String getValue() {

    if (ageAsString != null)
      return ageAsString;

    
    
    if (younger_exactly_older>0)
      return ">"+age.getValue();
    if (younger_exactly_older<0)
      return "<"+age.getValue();
    return age.getValue();
  }

  
  public void setValue(String newValue) {
    String old = getValue();
    
    
    if (newValue.startsWith(">")) {
      newValue = newValue.substring(1);
      younger_exactly_older = 1;
    } else if (newValue.startsWith("<")) {
      newValue = newValue.substring(1);
      younger_exactly_older = -1;
    }

    if (age.setValue(newValue))
      ageAsString = null;
    else
      ageAsString = newValue;
    
    
    propagatePropertyChanged(this, old);
    
    
  }
  
  public void setValue(Delta age) {
    String old = getValue();
    this.age.setValue(age);
    
    propagatePropertyChanged(this, old);
  }

  
  public boolean updateAge() {
    
    String old  = getValue();

    
    Delta delta = Delta.get(getEarlier(), getLater());
    if (delta == null)
      return false;
      
    age = delta;
    younger_exactly_older = 0;
    ageAsString = null;

    
    propagatePropertyChanged(this, old);
    
    
    return true;
  }

  
  public int compareTo(Property other) {
    if (!isValid()||!other.isValid())
      return super.compareTo(other);
    return age.compareTo(((PropertyAge)other).age);
  }

  
  public PointInTime getEarlier() {
    Entity e = getEntity();
    
    if (e instanceof Fam) {
      Property parent = getParent();
      if (parent.getTag().equals(PropertyHusband.TAG))
        e = ((Fam) e).getHusband();
      if (parent.getTag().equals(PropertyWife.TAG))
        e = ((Fam) e).getWife();
    }
    
    if (!(e instanceof Indi))
      return null;
    
    PropertyDate birth = ((Indi) e).getBirthDate();
    return birth != null ? birth.getStart() : null;
  }

  
  public PointInTime getLater() {
    Property parent = getParent();
    
    if (parent.getTag().equals(PropertyHusband.TAG) || parent.getTag().equals(PropertyWife.TAG)) {
      
      parent = parent.getParent();
    }
    
    if (!(parent instanceof PropertyEvent))
      return null;
    PropertyDate date = ((PropertyEvent) parent).getDate();
    
    return date != null ? date.getStart() : null;
  }

} 
