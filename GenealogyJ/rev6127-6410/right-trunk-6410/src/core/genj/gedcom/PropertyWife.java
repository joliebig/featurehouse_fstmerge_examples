
package genj.gedcom;



public class PropertyWife extends PropertyXRef {

  private final static TagPath
    PATH_INDIFAMS = new TagPath("INDI:FAMS");
  
  public final static String TAG = "WIFE";

  public final static String LABEL_MOTHER = Gedcom.resources.getString("WIFE.mother");
  
  
  public PropertyWife() {
    super("WIFE");
  }
  
  
  protected PropertyWife(String tag) {
    super(tag);
    assertTag("WIFE");
  }

  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    return resources.getString("prop.wife.veto");
  }

  
  public Indi getWife() {
    return (Indi)getTargetEntity();
  }

  
  public void link() throws GedcomException {

    
    Fam fam = null;
    try {
      fam = (Fam)getEntity();
    } catch (ClassCastException ex) {
    }
    if (fam==null)
      throw new GedcomException(resources.getString("error.noenclosingfam"));

    
    Property p;
    Property ps[];

    
    if (fam.getWife()!=null)
      throw new GedcomException(resources.getString("error.already.spouse", fam.getWife().toString(), fam.toString()));

    
    Indi wife = (Indi)getCandidate();

    
    if (fam.getHusband()==wife)
      throw new GedcomException(resources.getString("error.already.spouse", wife.toString(), fam.toString()));

    
    if (wife.isDescendantOf(fam))
      throw new GedcomException(resources.getString("error.already.descendant", wife.toString(), fam.toString()));

    
    ps = wife.getProperties(PATH_INDIFAMS);
    PropertyFamilySpouse pfs;
    for (int i=0;i<ps.length;i++) {
      pfs = (PropertyFamilySpouse)ps[i];
      if (pfs.isCandidate(fam)) {
        link(pfs);
        return;
      }
    }

    
    pfs = new PropertyFamilySpouse();
    wife.addProperty(pfs);
    link(pfs);

    
  }

  
  public String getTargetType() {
    return Gedcom.INDI;
  }

} 
