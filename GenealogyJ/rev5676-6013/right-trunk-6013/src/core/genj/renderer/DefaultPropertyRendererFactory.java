
package genj.renderer;

import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.renderer.PropertyRenderer.RenderDate;
import genj.renderer.PropertyRenderer.RenderEntity;
import genj.renderer.PropertyRenderer.RenderFile;
import genj.renderer.PropertyRenderer.RenderMLE;
import genj.renderer.PropertyRenderer.RenderPlace;
import genj.renderer.PropertyRenderer.RenderSecret;
import genj.renderer.PropertyRenderer.RenderSex;
import genj.renderer.PropertyRenderer.RenderXRef;


 class DefaultPropertyRendererFactory implements PropertyRendererFactory {

  
  private static PropertyRenderer[] renderers = new PropertyRenderer[]{
    new RenderSecret(),
    new RenderFile(),
    new RenderPlace(),
    new RenderMLE(),
    new RenderXRef(),
    new RenderDate(),
    new RenderSex(),
    new RenderEntity(),
    PropertyRenderer.DEFAULT
  };
  
  
  protected DefaultPropertyRendererFactory() {
  }

  
  public PropertyRenderer getRenderer(Property prop) {
    return getRenderer(null, prop);
  }
  
  
  public PropertyRenderer getRenderer(TagPath path, Property prop) {
    
    
    for (int i=0;i<renderers.length;i++) {
      PropertyRenderer renderer = renderers[i];
      if (renderer.accepts(path, prop))
        return renderer;
    }

    
    return PropertyRenderer.DEFAULT;
  }  
  
  
}
