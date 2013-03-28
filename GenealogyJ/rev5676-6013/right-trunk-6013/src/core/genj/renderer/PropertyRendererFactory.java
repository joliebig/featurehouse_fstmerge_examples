
package genj.renderer;

import genj.gedcom.Property;
import genj.gedcom.TagPath;


public interface PropertyRendererFactory {
  
  public PropertyRendererFactory DEFAULT = new DefaultPropertyRendererFactory();

  
  public PropertyRenderer getRenderer(Property prop);
  
  
  public PropertyRenderer getRenderer(TagPath path, Property prop);  
  
}
