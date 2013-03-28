
package genj.gedcom;

import genj.util.swing.ImageIcon;


public class PropertySource extends PropertyXRef {

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    
    meta.assertTag("SOUR");
    
    if (value.startsWith("@")&&value.endsWith("@"))
      return super.init(meta,value);
    
    return new PropertyMultilineValue().init(meta, value);
  }


  
  public String getTag() {
    return "SOUR";
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

