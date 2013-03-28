
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;

import java.util.logging.Level;
import java.util.logging.Logger;


public class CreateXReference extends CreateRelationship {
  
  private Property source;
  private String sourceTag;

  
  public CreateXReference(Property source, String sourceTag) {
    super(getName(source, sourceTag),source.getGedcom(), getTargetType(source, sourceTag));
    this.source = source;
    this.sourceTag = sourceTag;
  }
  
  
  private static String getTargetType(Property source, String sourceTag) {
    
    try {
      PropertyXRef sample = (PropertyXRef)source.getMetaProperty().getNested(sourceTag, false).create("@@");
      return sample.getTargetType();
    } catch (GedcomException e) {
      Logger.getLogger("genj.edit.actions").log(Level.SEVERE, "couldn't determine target type", e);
      throw new RuntimeException("Couldn't determine target type for source tag "+sourceTag);
    }
  }
  
  
  private static String getName(Property source, String sourceTag) {
    String targetType = getTargetType(source, sourceTag);
    if (targetType.equals(sourceTag))
      return Gedcom.getName(targetType);
    return Gedcom.getName(targetType) + " (" + Gedcom.getName(sourceTag) + ")";    
  }
  
  
  public String getDescription() {
    return resources.getString("create.xref.desc", new String[]{ Gedcom.getName(targetType), source.getEntity().toString()});
  }

  
  protected Property change(Entity target, boolean targetIsNew) throws GedcomException {
    
    
    PropertyXRef xref = (PropertyXRef)source.addProperty(sourceTag, '@'+target.getId()+'@');
    
    try {
      xref.link();
      xref.addDefaultProperties();
    } catch (GedcomException e) {
      source.delProperty(xref);
      throw e;
    }
    
    
    return targetIsNew ? xref.getTarget() : xref;
    
  }

}
