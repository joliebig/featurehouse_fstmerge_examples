
package genj.gedcom;

import java.util.List;

import genj.util.swing.ImageIcon;


public class PropertyChild extends PropertyXRef {

  private final static TagPath
    PATH_FAMCHIL = new TagPath("FAM:CHIL");
  
  public final static ImageIcon
    IMG_MALE    = Grammar.V55.getMeta(PATH_FAMCHIL).getImage("male"),
    IMG_FEMALE  = Grammar.V55.getMeta(PATH_FAMCHIL).getImage("female"),
    IMG_UNKNOWN = Grammar.V55.getMeta(PATH_FAMCHIL).getImage();

  
   PropertyChild() {
    super("CHIL");
  }
  
  
   PropertyChild(String tag) {
    super(tag);
    assertTag("CHIL");
  }
  
  
  public Indi getChild() {
    return (Indi)getTargetEntity();
  }

  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    return resources.getString("prop.chil.veto");
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

    
    Indi child = (Indi)getCandidate();

    
    
    if (child.isAncestorOf(fam)) 
      throw new GedcomException(resources.getString("error.already.ancestor", child.toString(), fam.toString()));

    
    
    
    List<PropertyFamilyChild> famcs = child.getProperties(PropertyFamilyChild.class);
    for (int i=0, j=famcs.size(); i<j; i++) {
      
      PropertyFamilyChild pfc = (PropertyFamilyChild)famcs.get(i);
      if (pfc.isCandidate(fam)) {
        link(pfc);
        return;
      }        
      
    }

    
    PropertyFamilyChild pfc = new PropertyFamilyChild();
    child.addProperty(pfc);
    link(pfc);

    
  }
  
  
  public static String getLabelChildAlreadyinFamily(Indi child, Fam fam) {
    return resources.getString("error.already.child", child.toString(), fam.toString());
  }

  
  public String getTargetType() {
    return Gedcom.INDI;
  }
  
  
  public ImageIcon getImage(boolean checkValid) {
     
    Indi child = getChild();
    if (child==null) return super.getImage(checkValid);
    switch (child.getSex()) {
      case PropertySex.MALE: return overlay(IMG_MALE);
      case PropertySex.FEMALE: return overlay(IMG_FEMALE);
      default: return overlay(IMG_UNKNOWN);
    }
  }

} 
