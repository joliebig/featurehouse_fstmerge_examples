
package genj.gedcom;

import genj.util.swing.ImageIcon;

import java.util.ArrayList;
import java.util.List;


public abstract class PropertyXRef extends Property {

  
  private PropertyXRef target = null;

  
  private String  value  = "";

  
  protected PropertyXRef() {
  }

  
   void beforeDelNotify() {

    
    if (target!=null) {
      PropertyXRef other = target;
      Property pother = other.getParent();
      unlink();
      
      
      pother.delProperty(other);
    }

    
    super.beforeDelNotify();
    
    
  }

  
  public Entity getTargetEntity() {
    return target==null ? null : target.getEntity();
  }
  
  
  protected Entity getCandidate() throws GedcomException {
    
    if (target!=null)
      throw new IllegalArgumentException("Already linked");
    
    Entity entity = getGedcom().getEntity(getTargetType(), value);
    if (entity==null)
      
      throw new GedcomException(resources.getString("error.notfound", Gedcom.getName(getTargetType()), value));
    return entity;
  }

  
  protected boolean isCandidate(Entity entity) {
    
    if (target!=null)
      return false;
    
    return value.length()==0 || entity.getId().equals(value);
  }

  
  public String getValue() {
    return target!=null ? '@'+target.getEntity().getId()+'@' : '@'+value+'@';
  }

  
  public boolean isValid() {
    return target!=null;
  }
  
  
  public abstract void link() throws GedcomException;
  
  
  protected void link(PropertyXRef target) {
    if (this.target!=null)
      throw new IllegalArgumentException("can't link while target!=null");
    if (target==null)
      throw new IllegalArgumentException("can't link to targe null");
    this.target = target;
    target.target = this;
    propagateXRefLinked(this, target);
  }
  
  
  
  public void unlink() {
    if (target==null)
      throw new IllegalArgumentException("can't unlink without target");
    PropertyXRef old = target;
    target.target = null;
    target = null;
    propagateXRefUnlinked(this, old);
  }

  
  public String getDisplayValue() {
    if (target==null)
      return getValue();
    return target.getEntity().toString();
  }
  
  
  protected String getForeignDisplayValue() {
    Entity entity = getEntity();
    Property parent = getParent();
    String by = parent!=entity ? entity.toString() + " - " + parent.getPropertyName() : entity.toString();
    return resources.getString("foreign.xref", by);
  }
  
    
  public PropertyXRef getTarget() {
    return target;
  }

  
  public void setValue(String set) {

    
    if (target!=null)
      return;
      
    
    String old = getParent()==null?null:getValue();

    
    value = set.replace('@',' ').trim();

    
    if (old!=null) propagatePropertyChanged(this, old);
    
    
  }
  
  
   Property init(MetaProperty meta, String value) throws GedcomException {
    meta.assertTag(getTag());
    
    
    value = value.trim();
    
    if (!(value.startsWith("@")&&value.endsWith("@")))
      throw new GedcomException(resources.getString("error.norefvalue", value, Gedcom.getName(getTag())));
    return super.init(meta, value);
  }

  
  public String toString() {
    Entity e = getTargetEntity();
    if (e==null) {
      return super.toString();
    }
    return e.toString();
  }

  
  public abstract String getTargetType();

  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    
    String key = "prop."+getTag().toLowerCase()+".veto";
    if (resources.contains(key))
      return resources.getString(key);
    
    return resources.getString("prop.xref.veto");
  }

  
  public static Entity[] getReferences(Entity ent) {
    List result = new ArrayList(10);
    
    List ps = ent.getProperties(PropertyXRef.class);
    for (int p=0; p<ps.size(); p++) {
    	PropertyXRef px = (PropertyXRef)ps.get(p);
      Property target = px.getTarget(); 
      if (target!=null) result.add(target.getEntity());
    }
    
    return (Entity[])result.toArray(new Entity[result.size()]);
  }

  
  public ImageIcon getImage(boolean checkValid) {
    return overlay(super.getImage(false));
  }
  
  
  protected ImageIcon overlay(ImageIcon img) {
    ImageIcon overlay = target!=null?MetaProperty.IMG_LINK:MetaProperty.IMG_ERROR;
    return img.getOverLayed(overlay);
  }
  
  
  public void setPrivate(boolean set, boolean recursively) {
    
  }

    
  public int compareTo(Property other) {
    
    
    PropertyXRef that = (PropertyXRef)other;
    
    
    if (this.getTargetEntity()==null||that.getTargetEntity()==null)
      return super.compareTo(that);

    
    
    
    return compare(getTargetEntity().toString(), that.getTargetEntity().toString());
  }

} 
