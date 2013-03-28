
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.IconValueAvailable;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyMultilineValue;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.util.Dimension2d;
import genj.util.swing.ImageIcon;
import genj.util.swing.UnitGraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;


public class PropertyRenderer {

  public final static PropertyRenderer DEFAULT = new PropertyRenderer();

  private final static String STARS = "*****";
  
  private final static int IMAGE_GAP = 4;
  
  
  private final static Dimension EMPTY_DIM = new Dimension(0,0);
  
    
  private final static ImageIcon broken = 
    new ImageIcon(PropertyRenderer.class, "Broken");

  public static final String HINT_KEY_TXT = "txt";
  public static final String HINT_KEY_IMG = "img";
  public static final String HINT_KEY_SHORT = "short";

  public static final String HINT_VALUE_TRUE = "yes";
  public static final String HINT_VALUE_FALSE = "no";
  
  
  public boolean accepts(TagPath path, Property prop) {
    
    return true;
  }

  
  public final Dimension2D getSize(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
    return getSizeImpl(font, context, prop, attributes, dpi);
  }
  
  protected Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
    return getSizeImpl(font, context, prop, prop.getDisplayValue(), attributes, dpi);

  }
  protected Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, String txt, Map<String,String> attributes, Point dpi) {
    double 
      w = 0,
      h = 0;
    
    if (!HINT_VALUE_FALSE.equals(attributes.get(HINT_KEY_TXT))&&txt.length()>0) {
      TextLayout layout = new TextLayout(txt, font, context);
      w += layout.getAdvance();
      h = Math.max(h, layout.getAscent() + layout.getDescent());
    }
    
    if (HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) {
      ImageIcon img = prop.getImage(false);
      LineMetrics lm = font.getLineMetrics("", context);
      float max = lm.getHeight();
      float scale = 1F;
      if (max<img.getIconHeight()) 
        scale = max/img.getIconHeight();
      w += (int)Math.ceil(img.getIconWidth()*scale) + IMAGE_GAP;
      h = Math.max(h, lm.getHeight());
    }
    
    
    return new Dimension2d(w,h);
  }

  
  public final void render(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
    renderImpl(g,bounds,prop,attributes,dpi);
  }
  
  protected void renderImpl(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
    renderImpl(g,bounds,prop,prop.getDisplayValue(),attributes,dpi);
  }
  
  
  protected void renderImpl(Graphics2D g, Rectangle bounds, Property prop, String txt, Map<String,String> attributes, Point dpi) {
    
    if (HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) 
      renderImpl(g, bounds, prop.getImage(false), dpi);
    
    if (!HINT_VALUE_FALSE.equals(attributes.get(HINT_KEY_TXT))) 
      renderImpl(g, bounds, txt, attributes);
    
  }
  
  
  protected void renderImpl(Graphics2D g, Rectangle bounds, ImageIcon img, Point dpi) {
    
    
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
  
  
  protected void renderImpl(Graphics2D g, Rectangle bounds, String txt, Map<String,String> attributes) {
    
    
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
  
  
  protected boolean isNullRenderer() {
    return false;
  }
  
  
   static class RenderPlace extends PropertyRenderer {
    
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertyPlace;
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      return super.getSizeImpl(font, context, prop, getText(prop, attributes), attributes, dpi);
    }

    
    public void renderImpl( Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      super.renderImpl(g, bounds, prop, getText(prop, attributes), attributes, dpi);
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
    
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertySex;
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      patch(attributes);
      return super.getSizeImpl(font, context, prop, value(prop, attributes), attributes, dpi);
    }

    
    public void renderImpl( Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      patch(attributes);
      super.renderImpl(g, bounds, prop, value(prop, attributes) ,attributes, dpi);
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
  
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertyMultilineValue;
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      
      
      if (!(prop instanceof MultiLineProperty))
        return super.getSizeImpl(font, context, prop, attributes, dpi);
      
      
      int lines = 0;
      double width = 0;
      double height = 0;
      MultiLineProperty.Iterator line = ((MultiLineProperty)prop).getLineIterator();
      do {
        lines++;
        Rectangle2D bounds = font.getStringBounds(line.getValue(), context);
        width = Math.max(width, bounds.getWidth());
        height += bounds.getHeight();
      } while (line.next());
      
      
      return new Dimension2d(width, height);
    }
  
    
    public void renderImpl( Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      
      
      if (!(prop instanceof MultiLineProperty)) {
        super.renderImpl(g, bounds, prop, attributes, dpi);
        return;
      }
      
      
      MultiLineProperty.Iterator line = ((MultiLineProperty)prop).getLineIterator();
      
      
      Graphics2D graphics = (Graphics2D)g;
      Rectangle clip = g.getClipBounds();
      
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

  
   static class RenderFile extends PropertyRenderer {

    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertyFile 
      || prop instanceof PropertyBlob 
      || (path!=null&&path.getLast().equals("FILE"));
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      
      
      ImageIcon img = getImage(prop, attributes);
      if (img==null) 
        return EMPTY_DIM;

      
      return img.getSizeInPoints(dpi);
        
    }
    
    
    public void renderImpl(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      
      
      ImageIcon img = getImage(prop, attributes);
      if (img==null) return;
      
      
      UnitGraphics ug = new UnitGraphics(g, 1, 1);
      ug.pushTransformation();
      ug.setColor(Color.black);
      ug.translate(bounds.x, bounds.y);
      
      
      
      Point idpi = img.getResolution();
      double
       scalex = 1,
       scaley = 1;
      if (idpi!=null) {
       scalex *= (double)dpi.x/idpi.x;
       scaley *= (double)dpi.y/idpi.y;
      }
       
      
      
      
      double 
        w = img.getIconWidth ()*scalex,
        h = img.getIconHeight()*scaley;
      if (bounds.width<w||bounds.height<h) {
        double zoom = Math.min(
          bounds.width/w, bounds.height/h
        );
        scalex *= zoom;
        scaley *= zoom;
      }        
        
      
      ug.scale(scalex, scaley);
      ug.draw(img, 0, 0, 0, 0);
      
      
      ug.popTransformation();
         
      
    }

    
    private ImageIcon getImage(Property prop, Map<String,String> attributes) {
      
      ImageIcon result = null;
      if (prop instanceof IconValueAvailable) 
        result = ((IconValueAvailable)prop).getValueAsIcon();
      
      if (result==null&&HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) return broken;
      
      return result;
    }  
  
    
    protected boolean isNullRenderer() {
      return true;
    }

  } 

  
   static class RenderEntity extends PropertyRenderer {
  
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof Entity;
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      return super.getSizeImpl(font, context, prop, ((genj.gedcom.Entity)prop).getId(), attributes, dpi);
    }
  
    
    public void renderImpl(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      attributes.put("align", "right");
      super.renderImpl(g, bounds, prop, ((genj.gedcom.Entity)prop).getId(), attributes, dpi);
    }
    
  } 

  
   static class RenderXRef extends PropertyRenderer {
    
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertyXRef;
    }

    
  
  } 
      
  
   static class RenderSecret extends PropertyRenderer {
  
    
    public boolean accepts(TagPath path, Property prop) {
      return prop!=null && prop.isSecret();
    }

    
    public Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
      return super.getSizeImpl(font, context, prop, STARS, attributes, dpi);
    }
  
    
    public void renderImpl( Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      super.renderImpl(g, bounds, prop, STARS, attributes, dpi);
    }
    
  } 
      
  
   static class RenderDate extends PropertyRenderer {
    
    
    public boolean accepts(TagPath path, Property prop) {
      return prop instanceof PropertyDate;
    }

    
    public void renderImpl(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
      attributes.put("align", "right");
      super.renderImpl(g, bounds, prop, attributes, dpi);
    }
    
  } 

} 
