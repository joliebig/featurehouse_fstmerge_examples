
package genj.gedcom;

import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.util.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Indi extends Entity {
  
  private final static TagPath
    PATH_INDI = new TagPath("INDI"),
    PATH_INDIFAMS = new TagPath("INDI:FAMS"),
    PATH_INDIFAMC = new TagPath("INDI:FAMC"),
    PATH_INDIBIRTDATE = new TagPath("INDI:BIRT:DATE"),
    PATH_INDIDEATDATE = new TagPath("INDI:DEAT:DATE"),
    PATH_INDIDEAT = new TagPath("INDI:DEAT");

  public final static ImageIcon
    IMG_MALE    = Grammar.V55.getMeta(PATH_INDI).getImage("male"),
    IMG_FEMALE  = Grammar.V55.getMeta(PATH_INDI).getImage("female"),
    IMG_UNKNOWN = Grammar.V55.getMeta(PATH_INDI).getImage();
    
  public Indi() {
    super(Gedcom.INDI, "?");
  }
  
  
  public Indi(String tag, String id) {
    super(tag, id);
    assertTag(Gedcom.INDI);
  }

  
  public PropertyDate getBirthDate() {
    return getBirthDate( false );
  }

  
  public PropertyDate getBirthDate( boolean create )
  {
      PropertyDate date =  (PropertyDate)getProperty(PATH_INDIBIRTDATE);
      if( null != date || !create )
          return date;
      return (PropertyDate)setValue(PATH_INDIBIRTDATE,"");
  }

  
  public PropertyDate getDeathDate() {
      return getDeathDate( false );
  }

  
  public PropertyDate getDeathDate( boolean create ) {
      PropertyDate date =  (PropertyDate)getProperty(PATH_INDIDEATDATE);
      if( null != date || !create )
          return date;
      return (PropertyDate)setValue(PATH_INDIDEATDATE,"");
  }
  
  
  public Indi[] getSiblings(boolean includeMe) {
    
    
    Fam fam = getFamilyWhereBiologicalChild();
    if (fam==null)
      return new Indi[0];
    List<Indi> result  = new ArrayList<Indi>(fam.getNoOfChildren());
    Indi[] siblings = fam.getChildren();
    for (int s=0;s<siblings.length;s++)
      if (includeMe||siblings[s]!=this) 
        result.add(siblings[s]);
    
    
    return toIndiArray(result);
    
  }
  
  
  public Indi[] getYoungerSiblings() {
    
    
    Indi[] siblings = getSiblings(true);
    
    
    Arrays.sort(siblings, new PropertyComparator("INDI:BIRT:DATE"));
    
    
    List<Indi> result = new ArrayList<Indi>(siblings.length);
    for (int i=siblings.length-1;i>=0;i--) {
      if (siblings[i]==this)
        break;
      result.add(0, siblings[i]);
    }
    
    
    return toIndiArray(result);
  }
  
  
  public PropertyMultilineValue getAddress() {
  
    
    Property[] rs = getProperties("RESI");
    for (int i = 0; i<rs.length; i++){
      
        
        PropertyMultilineValue address = (PropertyMultilineValue)rs[i].getProperty("ADDR");
        if (address == null) 
          continue;

        
        PropertyDate date = (PropertyDate)rs[i].getProperty("DATE");
        if (date != null && date.isRange()) 
          continue;
        
        
        return address;
    }
    
    
    return null;
  }
  
  
  public Indi[] getOlderSiblings() {
    
    
    Indi[] siblings = getSiblings(true);
    
    
    Arrays.sort(siblings, new PropertyComparator("INDI:BIRT:DATE"));
    
    
    List<Indi> result = new ArrayList<Indi>(siblings.length);
    for (int i=0,j=siblings.length;i<j;i++) {
      if (siblings[i]==this)
        break;
      result.add(siblings[i]);
    }
    
    
    return toIndiArray(result);
  }
  
  
  public Indi[] getPartners() {
    
    Fam[] fs = getFamiliesWhereSpouse();
    List<Indi> l = new ArrayList<Indi>(fs.length);
    for (int f=0; f<fs.length; f++) {
      Indi p = fs[f].getOtherSpouse(this);
      if (p!=null) l.add(p);
    }
    
    Indi[] result = new Indi[l.size()];
    l.toArray(result);
    return result;
  }
  
  
  public List<Indi> getParents () {
    List<Indi> parents = new ArrayList<Indi>(2);
    for (Fam fam : getFamiliesWhereChild()) {
      Indi husband = fam.getHusband();
      if (husband!=null)
        parents.add(husband);
      Indi wife = fam.getWife();
      if (wife!=null)
        parents.add(wife);
    }
    return parents;
  }
  
  
  public Indi[] getChildren() {
    
    Fam[] fs = getFamiliesWhereSpouse();
    List<Indi> l = new ArrayList<Indi>(fs.length);
    for (int f=0; f<fs.length; f++) {
      Indi[]cs = fs[f].getChildren();
      for (int c=0;c<cs.length;c++)
          if (!l.contains(cs[c])) l.add(cs[c]);
    }
    
    Indi[] result = new Indi[l.size()];
    l.toArray(result);
    return result;
  }
  
  
  public String getBirthAsString() {

    PropertyDate p = getBirthDate();
    if (p==null) 
      return "";

    
    return p.getDisplayValue();
  }

  
  public String getDeathAsString() {

    PropertyDate p = getDeathDate();
    if (p==null) {
      return "";
    }

    
    return p.getDisplayValue();
  }

  
  public Fam[] getFamiliesWhereSpouse() {
    ArrayList<Fam> result = new ArrayList<Fam>(getNoOfProperties());
    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if ("FAMS".equals(prop.getTag())&&prop.isValid()) 
        result.add(((PropertyFamilySpouse)prop).getFamily());
    }
    return Fam.toFamArray(result);
  }

  
  
  public Fam[] getFamiliesWhereChild( ) {
    
    List<PropertyFamilyChild> famcs = getProperties(PropertyFamilyChild.class);
    List<Fam> result = new ArrayList<Fam>(famcs.size());
    for (int i=0; i<famcs.size(); i++) {
      PropertyFamilyChild famc = (PropertyFamilyChild)famcs.get(i);
      if (famc.isValid()&&!result.contains(famc))
        result.add((Fam)famc.getTargetEntity());
    }
    
    return Fam.toFamArray(result);
  }

  
  public Fam getFamilyWhereBiologicalChild( ) {

    
    Fam result = null;
    List<PropertyFamilyChild> famcs = getProperties(PropertyFamilyChild.class);
    for (int i=0; i<famcs.size(); i++) {
      PropertyFamilyChild famc = (PropertyFamilyChild)famcs.get(i);
      
      if (!famc.isValid()) continue;
      Boolean biological = famc.isBiological();
      
      if (Boolean.TRUE.equals(biological)) 
        return (Fam)famc.getTargetEntity();
      
      if (biological==null&&result==null)
        result = (Fam)famc.getTargetEntity();
    }
    
    
    return result;
  }
  
  
  public String getFirstName() {
    PropertyName p = (PropertyName)getProperty("NAME",true);
    return p!=null ? p.getFirstName() : "";  
  }

  
  public String getLastName() {
    PropertyName p = (PropertyName)getProperty("NAME",true);
    return p!=null ? p.getLastName() : ""; 
  }

  
  public String getNameSuffix() {
    PropertyName p = (PropertyName)getProperty("NAME",true);
    return p!=null ? p.getSuffix() : ""; 
  }
  
  
  public void setName(String first, String last) {
    PropertyName p = (PropertyName)getProperty("NAME",true);
    if (p==null) p = (PropertyName)addProperty(new PropertyName()); 
    p.setName(first, last);
  }
  
  
  public String getName() {
    PropertyName p = (PropertyName)getProperty("NAME",true);
    if (p==null)
      return "";
    return p.getDisplayValue();
  }
  
  
  public int getNoOfFams() {
    int result = 0;
    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if ("FAMS".equals(prop.getTag())&&prop.isValid())
        result++;
    }
    return result;
  }
  
  
  public int getSex() {
    PropertySex p = (PropertySex)getProperty("SEX",true);
    return p!=null ? p.getSex() : PropertySex.UNKNOWN;
  }
  
  
  public void setSex(int sex) {
    
    PropertySex p = (PropertySex)getProperty("SEX",false);
    
    if (p!=null&&!p.isValid())
      return;
    
    if (p==null) 
      p = (PropertySex)addProperty(new PropertySex());
    
    p.setSex(sex);
  }

  
  public boolean isDescendantOf(Indi indi) {
    return indi.isAncestorOf(this);
  }
  
  
  public boolean isAncestorOf(Indi indi) {
    
    
    
    return recursiveIsAncestorOf(indi, new HashSet<Indi>());
  }
  
  private boolean recursiveIsAncestorOf(Indi indi, Set<Indi> visited) {

    
    if (visited.contains(indi)) 
      return false;
    visited.add(indi);
    
    
    List<PropertyFamilyChild> famcs = indi.getProperties(PropertyFamilyChild.class);
    for (int i=0; i<famcs.size(); i++) {
      
      PropertyFamilyChild famc = (PropertyFamilyChild)famcs.get(i);
      
      
      if (!famc.isValid()||Boolean.FALSE.equals(famc.isBiological())) continue;
      
      Fam fam = famc.getFamily();
        
      
      Indi father = fam.getHusband();
      if (father!=null) {
        if (father==this)
          return true;
        if (recursiveIsAncestorOf(father, visited))
          return true;
      }
      Indi mother = fam.getWife();
      if (mother!=null) {
        if (mother==this)
          return true;
        if (recursiveIsAncestorOf(mother, visited))
          return true;
      }
        
    }
    
    
    return false;
    
  }

  
  public boolean isDescendantOf(Fam fam) {
    
    
    
    Indi[] children = fam.getChildren(false);
    for (int i = 0; i < children.length; i++) {
      Indi child = children[i];
      if (child==this)
        return true;
      if (child.isAncestorOf(this))
        return true;
    }
    
    
    return false;
  }
  
  
  public boolean isAncestorOf(Fam fam) {
    
    
    Indi husband = fam.getHusband();
    if (husband!=null) {
      if (husband==this) 
        return true;
      if (isAncestorOf(husband))
        return true;
    }
    
    
    Indi wife = fam.getWife();
    if (wife!=null) {
      if (wife==this) 
        return true;
      if (isAncestorOf(wife))
        return true;
    }
    
    
    return false;
  }
  

  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getName();
  }
  
  
   static Indi[] toIndiArray(Collection<Indi> c) {
    return (Indi[])c.toArray(new Indi[c.size()]);    
  }

  
  public ImageIcon getImage(boolean checkValid) {
    
    switch (getSex()) {
      case PropertySex.MALE: return IMG_MALE;
      case PropertySex.FEMALE: return IMG_FEMALE;
      default: return IMG_UNKNOWN;
    }
  }

  
  public String getAgeString(PointInTime pit) {
    Delta delta = getAge(pit);
    return delta!=null ? delta.toString() : "";
  }
  
  
  public Delta getAge(PointInTime pit) {
  
    
    PropertyDate pbirth = getBirthDate();
    if (pbirth==null) 
      return null;
    
    
    PointInTime start = pbirth.getStart();
    if (start.compareTo(pit)>0)
      return null;

    
    Delta delta = Delta.get(pbirth.getStart(), pit);
    if (delta==null)
      return null;

    
    return delta;
  }

  
  public Indi getBiologicalFather() {
    Fam f = getFamilyWhereBiologicalChild();
    return f!=null ? f.getHusband() : null;
  }

  
  public Indi getBiologicalMother() {
    Fam f = getFamilyWhereBiologicalChild();
    return f!=null ? f.getWife() : null;
  }  
  
  
  public boolean isDeceased() {
    
    PropertyEvent deat = (PropertyEvent)getProperty("DEAT");
    if (deat!=null) {
      
      if (deat.isKnownToHaveHappened().booleanValue())
        return true;
      
      Property date = deat.getProperty("DATE");
      if (date!=null&&date.isValid())
        return true;
    }
    
    
    
    PropertyDate birt = getBirthDate();
    if (birt!=null) {
      Delta delta = birt.getAnniversary();
      if (delta!=null && delta.getYears()>100)
        return true;
    }
    
    return false;
  }
  
} 