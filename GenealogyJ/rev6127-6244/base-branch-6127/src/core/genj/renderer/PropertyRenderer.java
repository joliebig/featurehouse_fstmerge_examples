
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyMultilineValue;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.util.Dimension2d;
import genj.util.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.util.Map;
import java.util.logging.Logger;


public class PropertyRenderer {
  
  protected final static Logger LOG = Logger.getLogger("genj.renderer");

  public final static PropertyRenderer DEFAULT = new PropertyRenderer();

  private final static String STARS = "*****";
  
  private final static int IMAGE_GAP = 4;
  
  public static final String HINT_KEY_TXT = "txt";
  public static final String HINT_KEY_IMG = "img";
  public static final String HINT_KEY_SHORT = "short";

  public static final String HINT_VALUE_TRUE = "yes";
  public static final String HINT_VALUE_FALSE = "no";
  
  
  public boolean accepts(Property root, TagPath path, Property prop) {
    
    return true;
  }

  
  public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
    if (prop==null)
      return new Dimension();
    return getSize(prop, prop.getDisplayValue(), attributes, graphics);
  }
  protected Dimension2D getSize(Property prop, String txt, Map<String,String> attributes, Graphics2D graphics) {
    
    double 
      w = 0,
      h = 0;
    
    FontMetrics fm = graphics.getFontMetrics();
    if (!HINT_VALUE_FALSE.equals(attributes.get(HINT_KEY_TXT))&&txt.length()>0) {
      w += fm.stringWidth(txt);
      h = Math.max(h, fm.getAscent() + fm.getDescent());
    }
    
    if (HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) {
      ImageIcon img = prop.getImage(false);
      float max = fm.getHeight();
      float scale = 1F;
      if (max<img.getIconHeight()) 
        scale = max/img.getIconHeight();
      w += (int)Math.ceil(img.getIconWidth()*scale) + IMAGE_GAP;
      h = Math.max(h, fm.getHeight());
    }
    
    
    return new Dimension2d(w,h);
  }

  
  public void render(Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
    if (prop==null)
      return;
    render(g,bounds,prop,prop.getDisplayValue(),attributes);
  }
  
  
  protected void render(Graphics2D g, Rectangle bounds, Property prop, String txt, Map<String,String> attributes) {
    
    if (HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) 
      render(g, bounds, prop.getImage(false));
    
    if (!HINT_VALUE_FALSE.equals(attributes.get(HINT_KEY_TXT))) 
      render(g, bounds, txt, attributes);
    
  }
  
  
  protected void render(Graphics2D g, Rectangle bounds, ImageIcon img) {
    
    
    if (bounds.getHeight()==0||bounds.getWidth()==0)
      return;
    
    
    int 
      w = img.getIconWidth(),
      max = g.getFontMetrics().getHeight();
    
    AffineTransform at = AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY());
    if (max<img.getIconHeight()) {
      float scale = max/(float)img.getIconHeight();
      at.scale(scale, scale);
      w = (int)Math.ceil(w*scale);
    }
    g.drawImage(img.getImage(), at, null);
    
    
    bounds.x += w+IMAGE_GAP;
    bounds.width -= w+IMAGE_GAP;
  }
  
  
  protected void render(Graphics2D g, Rectangle bounds, String txt, Map<String,String> attributes) {
    
    
    if (txt.length()==0)
      return;
    
    
    TextLayout layout = new TextLayout(txt, g.getFont(), g.getFontRenderContext());
    
    
    double x = bounds.getX();
    if ("right".equals(attributes.get("align"))) {
      if (layout.getAdvance()< bounds.getWidth())
        x = bounds.getMaxX() - layout.getAdvance();
    }
    
    
    layout.draw(g, (float)x, (float)bounds.getY()+layout.getAscent());
  }
  
  
   static class RenderPlace extends PropertyRenderer {
    
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof PropertyPlace;
    }

    
    public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
      return super.getSize(prop, getText(prop, attributes), attributes, graphics);
    }

    
    public void render( Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      super.render(g, bounds, prop, getText(prop, attributes), attributes);
    }
    
    private String getText(Property prop, Map<String,String> attributes) {

      Object j = attributes.get("jurisdiction");
      
      
      if (j!=null) {
        
        
        if ("0".equals(j))
          return ((PropertyPlace)prop).getFirstAvailableJurisdiction();

        
        String result = null;
        try {
            result = ((PropertyPlace)prop).getJurisdiction(Integer.parseInt(j.toString()));
        } catch (Throwable t) {
        }
        return result==null ? "" : result;
      }
      
      
      return prop.getDisplayValue();
    }
    
  } 
  
  
   static class RenderSex extends PropertyRenderer {
    
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof PropertySex;
    }

    
    public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
      patch(attributes);
      return super.getSize(prop, value(prop, attributes), attributes, graphics);
    }

    
    public void render( Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      patch(attributes);
      super.render(g, bounds, prop, value(prop, attributes) ,attributes);
    }
    
    private String value(Property sex, Map<String,String> attributes) {
      String result = sex.getDisplayValue();
      if (result.length()>0 && HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_SHORT)))
        result = result.substring(0,1);
      return result;
    }

    private void patch(Map<String,String> attributes) {
      if (!attributes.containsKey(HINT_KEY_TXT))
        attributes.put(HINT_KEY_TXT, HINT_VALUE_FALSE);
      if (!attributes.containsKey(HINT_KEY_IMG))
        attributes.put(HINT_KEY_IMG, HINT_VALUE_TRUE);
    }
  } 

  
   static class RenderMLE extends PropertyRenderer {
  
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof PropertyMultilineValue;
    }

    
    public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
      
      
      if (!(prop instanceof MultiLineProperty))
        return super.getSize(root, path, prop, attributes, graphics);
      
      
      FontMetrics fm = graphics.getFontMetrics();
      int lines = 0;
      double width = 0;
      double height = 0;
      MultiLineProperty.Iterator line = ((MultiLineProperty)prop).getLineIterator();
      do {
        lines++;
        width = Math.max(width, fm.stringWidth(line.getValue()));
        height += fm.getHeight();
      } while (line.next());
      
      
      return new Dimension2d(width, height);
    }
  
    
    public void render( Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      
      
      if (!(prop instanceof MultiLineProperty)) {
        super.render(g, bounds, root, path, prop, attributes);
        return;
      }
      
      
      MultiLineProperty.Iterator line = ((MultiLineProperty)prop).getLineIterator();
      
      
      Graphics2D graphics = (Graphics2D)g;
      Font font = g.getFont();
      FontRenderContext context = graphics.getFontRenderContext();

      float 
        x = (float)bounds.getX(),
        y = (float)bounds.getY();
      
      do {

        
        String txt = line.getValue();
        LineMetrics lm = font.getLineMetrics(txt, context);
        y += lm.getHeight();
        
        
        graphics.drawString(txt, x, y - lm.getDescent());
        
        
        if (y>bounds.getMaxY()) 
          break;
        
      } while (line.next());
      
    }
    
  } 

  
   static class RenderEntity extends PropertyRenderer {
  
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof Entity;
    }

    
    public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
      return super.getSize(prop, ((genj.gedcom.Entity)prop).getId(), attributes, graphics);
    }
  
    
    public void render(Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      attributes.put("align", "right");
      super.render(g, bounds, prop, ((genj.gedcom.Entity)prop).getId(), attributes);
    }
    
  } 

  
   static class RenderXRef extends PropertyRenderer {
    
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof PropertyXRef;
    }

    
  
  } 
      
  
   static class RenderSecret extends PropertyRenderer {
  
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop!=null && prop.isSecret();
    }

    
    public Dimension2D getSize(Property root, TagPath path, Property prop, Map<String,String> attributes, Graphics2D graphics) {
      return super.getSize(prop, STARS, attributes, graphics);
    }
  
    
    public void render( Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      super.render(g, bounds, prop, STARS, attributes);
    }
    
  } 
      
  
   static class RenderDate extends PropertyRenderer {
    
    
    public boolean accepts(Property root, TagPath path, Property prop) {
      return prop instanceof PropertyDate;
    }

    
    public void render(Graphics2D g, Rectangle bounds, Property root, TagPath path, Property prop, Map<String,String> attributes) {
      attributes.put("align", "right");
      super.render(g, bounds, root, path, prop, attributes);
    }
    
  } 

} 
