
package genj.gedcom;

import genj.crypto.Enigma;
import genj.util.ReferenceSet;
import genj.util.WordBuffer;

import java.util.ArrayList;
import java.util.List;


public class PropertyName extends Property {
  
  
  
  public static final int PREFIX_AS_IS = 0;
  public static final int PREFIX_LAST = 1;
  public static final int IGNORE_PREFIX = 2;

  private final static String 
    KEY_LASTNAME = "NAME.last",
    KEY_FIRSTNAME = "NAME.first";
  
  
  private String
    lastName  = "",
    firstName = "",
    suffix    = "";

  
  private String nameAsString;

  
   PropertyName(String tag) {
    super(tag);
  }
  
  
  public PropertyName() {
    super("NAME");
  }
  
  
  public PropertyName(String first, String last) {
    this();
    setName(first, last);
  }
  
  
  public int compareTo(Property other) {
  
    
    int result = compare(this.getLastName(), ((PropertyName)other).getLastName());
    if (result!=0)
      return result;
     
    
    return compare(this.getFirstName(), ((PropertyName)other).getFirstName());
  }

  
  public String getFirstName() {
    return firstName;
  }

  
  public boolean isValid() {
    
    if (!(getEntity() instanceof Indi||getEntity() instanceof Submitter)) return true;
    return nameAsString==null;
  }


  
  static public String getLabelForFirstName() {
    return Gedcom.getResources().getString("prop.name.firstname");
  }

  
  static public String getLabelForLastName() {
    return Gedcom.getResources().getString("prop.name.lastname");
  }

  
  static public String getLabelForSuffix() {
    return Gedcom.getResources().getString("prop.name.suffix");
  }

  
  public String getLastName() {
    return lastName;
  }

  
  public String getLastName(int prefixPresentation) {
    
    if (prefixPresentation == PropertyName.PREFIX_AS_IS) 
      return lastName;
    
    String last = lastName.replaceFirst("^[a-z ]*", "");
    last = last.replaceFirst("-", "");
    
    if (prefixPresentation == PropertyName.IGNORE_PREFIX) {
      last = last.replaceFirst("Hengevel[dt]", "Hengeveld/t");
      return last;
    }
    
    int diff = lastName.length() - last.length();
    if ( diff > 0 ) {
      last = last + ", "+ lastName.subSequence(0, diff);
    }
    return last;
  }

  
  public String getSuffix() {
    return suffix;
  }
  
  
  public String getNick() {
    return getPropertyValue("NICK");
  }
  
  public void setNick(String nick) {
    Property n = getProperty("NICK");
    if (n==null) {
      if (nick.length()==0)
        return;
      n = addProperty("NICK", nick);
    } else {
      n.setValue(nick);
    }
  }

  
  public String getName() {
    if (nameAsString!=null) 
      return nameAsString;
    if (firstName.length()==0) 
      return lastName;
    return lastName + ", " + firstName;
  }

  
  public String getValue() {

    if (nameAsString != null) 
      return nameAsString;
    
    WordBuffer wb = new WordBuffer();
    wb.append(firstName);
    
    if (lastName.length()>0||suffix.length()>0)
      wb.append("/"+lastName+"/");
    if (suffix.length()>0)
      wb.append(suffix);
    return wb.toString();
  }
  
  
  public String getDisplayValue() {
    
    if (isSecret())
      return "";
    
    
    if (nameAsString!=null)
      return nameAsString;
    
    WordBuffer b = new WordBuffer();
    
    if (Options.getInstance().nameFormat==1) {
      
      String last = getLastName();
      if (last.length()==0) last = "?";
      b.append(last);
      b.append(getSuffix());
      b.setFiller(", ");
      b.append(getFirstName());
      
    } else {
      
      b.append(getFirstName());
      b.append(getLastName());
      
    }
    
    return b.toString();
  }

  
  public PropertyName setName(String setLast) {
    return setName(firstName,setLast,suffix);
  }

  
  public PropertyName setName(String setFirst, String setLast) {
    return setName(setFirst,setLast,suffix);
  }

  
  public PropertyName setName(String setFirst, String setLast, String setSuffix) {
    return setName(setFirst, setLast, setSuffix, false);
  }
  
  
  public PropertyName setName(String first, String last, String suff, boolean replaceAllLastNames) {

    
    boolean hasParent = getParent()!=null;
    String old = hasParent ? getValue() : null;

    
    if (Options.getInstance().isUpperCaseNames)
      last = last.toUpperCase();
    
    
    
    
    first = first.trim().intern();
    last = last.trim().intern();
    suff = suff.trim();

    
    if (replaceAllLastNames) {
      
      Property[] others = getSameLastNames();
      for (int i=0;i<others.length;i++) {
        Property other = others[i];
        if (other instanceof PropertyName&&other!=this) {
          ((PropertyName)other).setName(last);
        }
      }
    }    
    
    
    remember(first, last);
    
    
    if (hasParent) {
      boolean add = Options.getInstance().isAddGivenSurname;
      Property givn = getProperty("GIVN");
      if (add || givn!=null) {
        if (givn==null)
          givn = addProperty("GIVN", first);
        else
          givn.setValue(first);
      }
      Property surn = getProperty("SURN");
      if (add || surn!=null) {
        if (surn==null)
          surn = addProperty("SURN", last);
        else
          surn.setValue(last);
      }
    }
    
    
    nameAsString=null;
    lastName  = last;
    firstName = first;
    suffix    = suff;

    
    if (old!=null) propagatePropertyChanged(this, old);
    
    
    return this;
  }
  
  
   void afterAddNotify() {
    
    super.afterAddNotify();
    
    remember(firstName, lastName);
    
  }
  
  
   void beforeDelNotify() {
    
    remember("", "");
    
    super.beforeDelNotify();
    
  }


  
  public void setValue(String newValue) {
    
    
    if (Enigma.isEncrypted(newValue)) {
      setName("","","");
      nameAsString=newValue;
      return;
    }

    
    if (newValue.indexOf('/')<0) {
      setName(newValue, "", "");
      return;
    }

    
    String f = newValue.substring( 0 , newValue.indexOf('/') ).trim();
    String l = newValue.substring( newValue.indexOf('/') + 1 );

    
    if (l.indexOf('/') == -1)  {
      setName("","","");
      nameAsString=newValue;
      return;
    }

    
    suffix = l.substring( l.indexOf('/') + 1 );
    l = l.substring( 0 , l.indexOf('/') );

    
    setName(f,l,suffix);
    
    
  }
  
  
  public List<String> getLastNames(boolean sortByName) {
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return new ArrayList<String>(0);
    return getLastNames(gedcom, sortByName);
  }

  
  public List<String> getFirstNames(boolean sortByName) {
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return new ArrayList<String>(0);
    return getFirstNames(gedcom, sortByName);
  }

  
  public static List<String> getLastNames(Gedcom gedcom, boolean sortByName) {
    return gedcom.getReferenceSet(KEY_LASTNAME).getKeys(sortByName ? gedcom.getCollator() : null);
  }

  
  public static List<String> getFirstNames(Gedcom gedcom, boolean sortByName) {
    return gedcom.getReferenceSet(KEY_FIRSTNAME).getKeys(sortByName ? gedcom.getCollator() : null);
  }

  
  public int getLastNameCount() {
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return 0;
    return getLastNameCount(gedcom, getLastName());
  }
  
  
  public static int getLastNameCount(Gedcom gedcom, String last) {
    return gedcom.getReferenceSet(KEY_LASTNAME).getReferences(last).size();
  }
  
  
  public Property[] getSameLastNames() {
    return toArray(getGedcom().getReferenceSet(KEY_LASTNAME).getReferences(getLastName()));
  }
  
  private void remember(String newFirst, String newLast) {
    
    Gedcom gedcom = getGedcom();
    if (gedcom==null)
      return;
    
    ReferenceSet<String, Property> refSet = gedcom.getReferenceSet(KEY_LASTNAME);
    if (lastName.length()>0) refSet.remove(lastName, this);
    if (newLast.length()>0) refSet.add(newLast, this);
    
    refSet = gedcom.getReferenceSet(KEY_FIRSTNAME);
    if (firstName.length()>0) refSet.remove(firstName, this);
    if (newFirst.length()>0) refSet.add(newFirst, this);
    
  }
} 
