
package genj.gedcom;

import java.util.List;


public class PropertyFamilyChild extends PropertyXRef {

  
   PropertyFamilyChild() {
    super("FAMC");
  }
  
  
   PropertyFamilyChild(String tag) {
    super(tag);
    assertTag("FAMC");
  }
  
  
  public Boolean isBiological() {
    
    String parent = getParent().getTag();
    if ("ADOP".equals(parent))
      return Boolean.FALSE;
    
    if ("BIRT".equals(parent))
      return Boolean.TRUE;
    
    Property pedi = getProperty("PEDI");
    if (pedi!=null) {
      String value = pedi.getValue();
      if ("birth".equals(value)) return Boolean.TRUE;
      if ("adopted".equals(value)) return Boolean.FALSE;
      if ("foster".equals(value)) return Boolean.FALSE; 
      if ("sealing".equals(value)) return Boolean.FALSE;
    }
    
    return null;
  }

  
  protected String getForeignDisplayValue() {
    
    Property adop = getParent();
    if (adop instanceof PropertyEvent&&adop.getTag().equals("ADOP"))
      return resources.getString("foreign.ADOP", getEntity().toString());
    
    return super.getForeignDisplayValue();
  }
  
  
  public Fam getFamily() {
    return (Fam)getTargetEntity();
  }

  
  public void link() throws GedcomException {

    
    Indi indi;
    try {
      indi = (Indi)getEntity();
    } catch (ClassCastException ex) {
      throw new GedcomException(resources.getString("error.noenclosingindi"));
    }
    
    
    Fam fam = (Fam)getCandidate();

    
    
    if (indi.isAncestorOf(fam))
      throw new GedcomException(resources.getString("error.already.ancestor", indi.toString(), fam.toString() ));

    
    
    
    
    
    
    
    
    List<PropertyChild> childs = fam.getProperties(PropertyChild.class);
    for (PropertyChild prop : childs) {
      if (prop.isCandidate(indi)) {
        link(prop);
        return;
      }
    }

    
    PropertyXRef xref = new PropertyChild();
    fam.addProperty(xref);
    link(xref);

    
  }

  
  public String getTargetType() {
    return Gedcom.FAM;

  }

} 
