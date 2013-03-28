
package genj.gedcom;


public class PropertyFamilySpouse extends PropertyXRef {

  
  public PropertyFamilySpouse() {
  }
  
  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    return resources.getString("prop.fams.veto");
  }

  
  public Fam getFamily() {
    return (Fam)getTargetEntity();
  }

  
  public String getTag() {
    return "FAMS";
  }

  
  public void link() throws GedcomException {

    
    Indi indi;
    try {
      indi = (Indi)getEntity();
    } catch (ClassCastException ex) {
      throw new GedcomException(resources.getString("error.noenclosingindi"));
    }

    
    Property p;
    Gedcom gedcom = getGedcom();

    
    Fam fam = (Fam)getCandidate();

    
    Indi husband = fam.getHusband();
    Indi wife    = fam.getWife();

    if ((husband!=null)&&(wife!=null))
      throw new GedcomException(resources.getString("error.already.spouses", fam));

    if ((husband==indi)||(wife==indi))
      throw new GedcomException(resources.getString("error.already.spouse", new String[]{ indi.toString(), fam.toString()}));

    Fam[] familiesWhereChild = indi.getFamiliesWhereChild();
    for (int i=0; i<familiesWhereChild.length; i++) {
      if (familiesWhereChild[i]==fam)
        throw new GedcomException(resources.getString("error.already.child", new String[]{ indi.toString(), fam.toString()}));
    }
    
    
    if (indi.isDescendantOf(fam)) 
      throw new GedcomException(resources.getString("error.already.descendant", new String[]{ indi.toString(), fam.toString()}));
    
    
    if (indi.getSex()==PropertySex.UNKNOWN) 
      indi.setSex(husband==null ? PropertySex.MALE : PropertySex.FEMALE);

    
    
    Property[] husbands = fam.getProperties("HUSB", false);
    for (int i=0;i<husbands.length;i++) {
      PropertyHusband ph = (PropertyHusband)husbands[i];
      if (ph.isCandidate(indi)) {
        link(ph);
        return;
      }
    }
    
    Property[] wifes = fam.getProperties("WIFE", false);
    for (int i=0;i<wifes.length;i++) {
      PropertyWife pw = (PropertyWife)wifes[i];
      if (pw.isCandidate(indi)) {
        link(pw);
        return;
      }
    }
    
    
    if (indi.getSex()==PropertySex.MALE) {
      
      if (husband!=null&&husband.getSex()!=PropertySex.MALE)
        fam.swapSpouses();
      
      PropertyXRef backref = new PropertyHusband();
      fam.addProperty(backref);
      link(backref);
    } else {
      
      if (wife!=null)
        fam.swapSpouses();
      
      PropertyXRef backref = new PropertyWife();
      fam.addProperty(backref);
      link(backref);
    }

    
  }

  
  public String getTargetType() {
    return Gedcom.FAM;

  }
  
} 
