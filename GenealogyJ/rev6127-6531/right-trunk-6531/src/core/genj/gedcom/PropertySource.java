
package genj.gedcom;

import genj.util.swing.ImageIcon;


public class PropertySource extends PropertyXRef {

  
  public PropertySource(String tag) {
    super(tag);
    assertTag("SOUR");
  }
  
  
  public void link() throws GedcomException {

    
    Source source = (Source)getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    source.addProperty(fxref);

    
    link(fxref);

    
  }

  
  public String getTargetType() {
    return Gedcom.SOUR;
  }

  
  protected ImageIcon overlay(ImageIcon img) {
    
    if (super.getTargetEntity()!=null)
      return super.overlay(img);
    
    return img;
  }
} 

