
package genj.gedcom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Fam extends Entity {
  
  public final static TagPath
    PATH_FAMMARRDATE = new TagPath("FAM:MARR:DATE"),
    PATH_FAMMARRPLAC = new TagPath("FAM:MARR:PLAC"),
    PATH_FAMDIVDATE  = new TagPath("FAM:DIV:DATE"),
    PATH_FAMDIVPLAC  = new TagPath("FAM:DIV:PLAC");

  private final static TagPath
    SORT_SIBLINGS = new TagPath("CHIL:*:..:BIRT:DATE");

  
  private class CHILComparator extends PropertyComparator {
    
    CHILComparator() {
      super(SORT_SIBLINGS);
    }
    
    public int compare(Property p1, Property p2) {
      int result = super.compare(p1, p2);
      return result!=0 ? result : getPropertyPosition(p1) - getPropertyPosition(p2);
    }
  };

  
  public Indi getChild(int which) {

    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if ("CHIL".equals(prop.getTag())&&prop.isValid()) {
        if (which==0)
          return ((PropertyChild)prop).getChild();
        which--;
      }
    }
    
    throw new IllegalArgumentException("no such child");
  }

  
  public Indi[] getChildren() {
    return getChildren(true);
  }
  
  
  public Indi[] getChildren(boolean sorted) {

    
    List CHILs = new ArrayList(getNoOfProperties());
    for (Iterator it = getProperties(PropertyChild.class).iterator(); it.hasNext(); ) {
      PropertyChild prop = (PropertyChild)it.next();
      if (prop.isValid()) {
        CHILs.add(prop);
        
        
        if (sorted) {
          Property sortby = prop.getProperty(SORT_SIBLINGS); 
          if (sortby==null||!sortby.isValid())
            sorted = false;
        }
      }
    }
    
    
    if (sorted) 
      Collections.sort(CHILs, new CHILComparator());
    
    
    List children = new ArrayList(CHILs.size());
    for (int i=0;i<CHILs.size();i++) {
      Indi child = ((PropertyChild)CHILs.get(i)).getChild();
      if (!children.contains(child))
          children.add(child);
    }
    
    
    
    return Indi.toIndiArray(children);
  }

  
  public Indi getHusband() {
    Property husb = getProperty("HUSB", true);
    if (husb instanceof PropertyHusband)
      return ((PropertyHusband)husb).getHusband();
    return null;    
  }

  
  public int getNoOfChildren() {
    int result = 0;
    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if (prop.getClass()==PropertyChild.class&&prop.isValid())
        result++;
    }
    return result;
  }
  
  
  public int getNoOfSpouses() {
    int result = 0;
    if (getHusband()!=null) result++;
    if (getWife   ()!=null) result++;
    return result;
  } 
  
  
  public Indi getSpouse(int which) {
    Indi husband = getHusband();
    if (husband!=null) {
      if (which==0)
        return husband;
      which--;
    }
    Indi wife = getWife();
    if (wife!=null) {
      if (which==0)
        return wife;
      which--;
    }
    throw new IllegalArgumentException("No such spouse");
  }

  
  public Indi getOtherSpouse(Indi spouse) {
    Indi wife = getWife();
    if (wife==spouse) return getHusband();
    return wife;
  }

  
  public Indi getWife() {
    
    Property wife = getProperty("WIFE", true);
    if (wife instanceof PropertyWife) 
      return ((PropertyWife)wife).getWife();
    return null;
  }

  
  public PropertyXRef setHusband(Indi husband) throws GedcomException {
    
    
    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if ("HUSB".equals(prop.getTag())&&prop.isValid()) {
        delProperty(prop);
        break;
      }
    }
    
    
    if (husband==null)
      return null;
    
    
    PropertyHusband ph = new PropertyHusband(husband.getId());
    addProperty(ph);

    
    try {
      ph.link();
    } catch (GedcomException ex) {
      delProperty(ph);
      throw ex;
    }
    
    
    if (husband.getSex()!=PropertySex.MALE)
      husband.setSex(PropertySex.MALE);

    
    return ph;
  }

  
  public PropertyXRef setWife(Indi wife) throws GedcomException {

    
    for (int i=0,j=getNoOfProperties();i<j;i++) {
      Property prop = getProperty(i);
      if ("WIFE".equals(prop.getTag())&&prop.isValid()) {
        delProperty(prop);
        break;
      }
    }
    
    
    if (wife==null)
      return null;
    
    
    PropertyWife pw = new PropertyWife(wife.getId());
    addProperty(pw);

    
    try {
      pw.link();
    } catch (GedcomException ex) {
      delProperty(pw);
      throw ex;
    }

    
    if (wife.getSex()!=PropertySex.FEMALE)
      wife.setSex(PropertySex.FEMALE);

    
    return pw;
  }

  
  public PropertyXRef setSpouse(Indi spouse) throws GedcomException {  
    
    Indi husband = getHusband();
    Indi wife = getWife();
    
    
    if (husband!=null&&wife!=null)
      throw new GedcomException(resources.getString("error.already.spouses", this));

    
    PropertyXRef HUSBorWIFE;
    switch (spouse.getSex()) {
      default:
      case PropertySex.UNKNOWN:
        
        HUSBorWIFE = husband!=null ? setWife(spouse) : setHusband(spouse);
        break;
      case PropertySex.MALE:
        
        HUSBorWIFE = setHusband(spouse);
        
        if (husband!=null)
          setWife(husband);
        break;
      case PropertySex.FEMALE:
        
        HUSBorWIFE = setWife(spouse);
        
        if (wife!=null)
          setHusband(wife);
        break;
    }
    
    
    return HUSBorWIFE;
  }
  
  
  public PropertyXRef addChild(Indi newChild) throws GedcomException {

    
    PropertyChild pc = new PropertyChild(newChild.getId());
    addProperty(pc);

    
    try {
      pc.link();
    } catch (GedcomException ex) {
      delProperty(pc);
      throw ex;
    }

    return pc;
  }

  
   static Fam[] toFamArray(Collection c) {
    return (Fam[])c.toArray(new Fam[c.size()]);    
  }

  
  protected String getToStringPrefix(boolean showIds, boolean showAsLink) {
    
    StringBuffer result = new StringBuffer();

    Indi husband = getHusband();
    if (husband!=null) {
      result.append(husband.toString(showIds, showAsLink));
      result.append(Options.getInstance().getTxtMarriageSymbol());
    }
    
    Indi wife = getWife();
    if (wife!=null) {
      result.append(wife.toString(showIds, showAsLink));
    }

    
    return result.toString();
  }
  
  
  public PropertyDate getMarriageDate() {
      return getMarriageDate(false);
    
  }

  
  public PropertyDate getMarriageDate(boolean create) {
      PropertyDate date = (PropertyDate)getProperty(PATH_FAMMARRDATE);
      if( null != date || !create )
          return date;
      setValue(PATH_FAMMARRDATE,"");
      return (PropertyDate)getProperty(PATH_FAMMARRDATE);
  }


  public PropertyDate getDivorceDate() {
    
    return (PropertyDate)getProperty(PATH_FAMDIVDATE);
  }

  
  public void swapSpouses() throws GedcomException {
    
    Indi 
      husband = getHusband(),
      wife = getWife();

    setWife(null);
    setHusband(null);
      
    if (wife!=null)
      setHusband(wife);
    if (husband!=null)
      setWife(husband);
      
  }
  
  protected String getIdLinkFormat() {
 return genj.report.Options.getInstance().getLinkToFam();
  }


} 
