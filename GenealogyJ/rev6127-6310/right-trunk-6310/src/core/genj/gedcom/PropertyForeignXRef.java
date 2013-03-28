
package genj.gedcom;

import genj.util.swing.ImageIcon;


 class PropertyForeignXRef extends PropertyXRef {

  
   PropertyForeignXRef() {
    super("XREF");
  }
  
  
  public String getValue() {
    Entity entity = getTargetEntity();
    return entity==null ? "" : '@'+getTargetEntity().getId()+'@';
  }
  
  
  
  public String getDisplayValue() {
    
    PropertyXRef target = getTarget();
    return target != null ? target.getForeignDisplayValue() : "";
  }

  
  public void link() {
    throw new RuntimeException("link is not support by ForeignXRefs");
  }

  
  public void setValue(String newValue) {
    
  }

  
  public ImageIcon getImage(boolean checkValid) {
    
    PropertyXRef target = getTarget();
    return target != null ? overlay(target.getEntity().getImage(false)) : MetaProperty.IMG_ERROR;
  }

  
  public String getTargetType() {
    throw new IllegalArgumentException("getTargetType is not support by ForeignXRefs");
  }

  
  public boolean isValid() {
    return true; 
  }

  
  public boolean isTransient() {
    return true; 
  }

} 
