
package genj.gedcom;


public class PropertyRelationship extends PropertyChoiceValue {

  
  private TagPath anchor = null;

  
  public String getValue() {
    String value = super.getValue();
    TagPath anchor = getAnchor();
    if (anchor!=null&&anchor.length()>0)
      value += '@' + anchor.toString();
    return value;
  }
  
  public String getDisplayValue() {
    return super.getValue();
  }
  
  
  public void setValue(String value) {

    
    int i = value.lastIndexOf('@');
    if (i>=0) {
      try {
        anchor = new TagPath(value.substring(i+1));
        
        if (!getAnchor().equals(anchor)) {
          PropertyAssociation asso = (PropertyAssociation)getParent();
          Property target = asso.getTarget();
          asso.unlink();
          target.getParent().delProperty(target);
          asso.link();
        }
      } catch (Throwable t) {
      }
      value = value.substring(0,i);
    }
    
    super.setValue(value);
  }
  
  
   Property getTarget() {
    
    Property parent = getParent();
    if (parent instanceof PropertyAssociation)
      return ((PropertyAssociation)parent).getTarget();
    return null;
  }
  
  
   TagPath getAnchor() {

    
    Property target = getTarget();
    if (target!=null) {
      Property panchor = target.getParent();
      if (!(panchor instanceof Entity)&&panchor!=null) {
        
        TagPath result = panchor.getPath(false);
        
        return panchor.getEntity().getProperty(result) == panchor ? result : panchor.getPath(true); 
      }
    }
    
    
    return anchor;

  }

} 
