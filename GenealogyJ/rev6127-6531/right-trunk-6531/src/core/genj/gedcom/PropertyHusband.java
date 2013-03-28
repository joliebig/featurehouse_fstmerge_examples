
package genj.gedcom;


public class PropertyHusband extends PropertyXRef {

  public final static String LABEL_FATHER = Gedcom.resources.getString("HUSB.father");

  
   PropertyHusband() {
    super("HUSB");
  }
  
  
   PropertyHusband(String tag) {
    super(tag);
    assertTag("HUSB");
  }

  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    return resources.getString("prop.husb.veto");
  }

  
  public Indi getHusband() {
    return (Indi)getTargetEntity();
  }

  
  public void link() throws GedcomException {

    
    Fam fam;
    try {
      fam = (Fam)getEntity();
    } catch (ClassCastException ex) {
      throw new GedcomException(resources.getString("error.noenclosingfam"));
    }

    
    Property p;
    Property ps[];

    
    if (fam.getHusband()!=null)
      throw new GedcomException(resources.getString("error.already.spouse", fam.getHusband().toString(), fam.toString()));

    
    Indi husband = (Indi)getCandidate();

    
    if (fam.getWife()==husband)
      throw new GedcomException(resources.getString("error.already.spouse", husband.toString(), fam.toString()));

    
    if (husband.isDescendantOf(fam))
      throw new GedcomException(resources.getString("error.already.descendant", husband.toString(), fam.toString()));
    
    
    ps = husband.getProperties(new TagPath("INDI:FAMS"));
    PropertyFamilySpouse pfs;
    for (int i=0;i<ps.length;i++) {
      pfs = (PropertyFamilySpouse)ps[i];
      if (pfs.isCandidate(fam)) {
        link(pfs);
        return;
      }
    }

    
    pfs = new PropertyFamilySpouse();
    husband.addProperty(pfs);
    link(pfs);

    
  }

  
  public String getTargetType() {
    return Gedcom.INDI;
  }
  
} 
