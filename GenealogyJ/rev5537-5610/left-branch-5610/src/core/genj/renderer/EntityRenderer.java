
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Dimension2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Dimension2D;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.Position.Bias;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.ParserDelegator;


public class EntityRenderer {
  
  
  
  static {
    new ParserDelegator();
  }
  
  
  private static final int 
    PROP_IMAGE_WIDTH  = Indi.IMG_MALE.getIconWidth()+4,
    PROP_IMAGE_HEIGHT = Indi.IMG_MALE.getIconHeight();
  
  
  private static final Segment EMPTY_SEGMENT = new Segment(); 
  
  
  private RootView root;
  
  
  private HTMLDocument doc = new MyHTMLDocument();
  
  
  private MyHTMLFactory factory = new MyHTMLFactory();
  
  
  private static DTD dtd = null;
  
  
  private FontRenderContext context;
  
  
  private Entity entity;
  
  
  private List propViews = new ArrayList(16);
  
  
  private List tableViews = new ArrayList(4);
  
  
  private boolean isDebug = false;
  
  
  private Point dpi = new Point(96,96);
  
  
  private boolean isScaleFonts = false;

    
  public EntityRenderer(Blueprint bp) {
    this(bp, Options.getInstance().getDefaultFont());
  }
    
    
  public EntityRenderer(Blueprint bp, Font font) {

    
    StringBuffer html = new StringBuffer();
    html.append("<html><head><style type=\"text/css\">");
    if (font!=null) {
      html.append(" body { font-family: \""+font.getFamily()+"\"; font-size: "+font.getSize()+"pt; } "    );
    }
    html.append(" table { border-style: solid;}" );
    html.append(" td  { border-style: solid;  }" );
    html.append("</style></head><body>");
    html.append(bp.getHTML());
    html.append("</body></html>");

    
    try {
      
      
      
      
      
      
      
      
      
      
      
      
      MyHTMLReader reader = new MyHTMLReader(doc);
      
      
      new MyDocumentParser(DTD.getDTD("html32")).parse(new StringReader(html.toString()), reader, false);
      
      
      reader.flush();      
      
    } catch (Throwable t) {
      
    }

    
    root = new RootView(factory.create(doc.getDefaultRootElement()));

    
  }
  
    public EntityRenderer setResolution(Point set) {
    dpi = new Point(set);
    
    return this;
  }
  
  
  public EntityRenderer setResolution(Dimension set) {
    dpi = new Point(set.width, set.height);
    
    return this;
  }
  
  
  public EntityRenderer setScaleFonts(boolean set) {
    isScaleFonts = set;
    
    return this;
  }
  
  
  public void render(Graphics g, Entity e, Rectangle r) {
    
    
    entity = e;
    context = ((Graphics2D)g).getFontRenderContext();
    
    
    Iterator pv = propViews.iterator();
    while (pv.hasNext()) {
      ((PropertyView)pv.next()).invalidate();
    }
    
    
    Iterator tv = tableViews.iterator();
    while (tv.hasNext()) {
      
      try {
        ((View)tv.next()).replace(0,0,null);
      } catch (Throwable t) {
      }
    }
    
    
    root.setSize((float)r.getWidth(),(float)r.getHeight());
    
    
    Rectangle oc = g.getClipBounds();
    g.clipRect(r.x,r.y,r.width,r.height);

    
    root.paint(g, r);
    
    
    g.setClip(oc.x,oc.y,oc.width,oc.height);
    
    
  }
  
    public void setDebug(boolean set) {
    isDebug = set;
  }

      private class MyHTMLDocument extends HTMLDocument {
    
    public Font getFont(AttributeSet attr) {
      Font font = super.getFont(attr);
      if (isScaleFonts) {
        float factor = dpi.y/72F; 
        font = font.deriveFont(factor*font.getSize2D());
      }
      return font;
    }
  } 
  
  
  private class MyDocumentParser extends DocumentParser {
        private MyDocumentParser(DTD dtd) {
      super(dtd);
      
      strict = true;
      
    }
  } 

  
  private static class MyHTMLReader extends HTMLDocument.HTMLReader {
    
    private boolean skipContent = false;
        protected MyHTMLReader(HTMLDocument doc) {
      doc.super(0);
    }
    
    protected void blockClose(Tag t) {
      
      
      
      
      
      skipContent = true;
      
      super.blockClose(t);
      
      skipContent = false;
    }
    
    protected void addContent(char[] data, int offs, int length, boolean generateImpliedPIfNecessary) {
      if (!skipContent) super.addContent(data, offs, length, generateImpliedPIfNecessary);
    }
    
  } 
  
  
  private class MyHTMLFactory extends HTMLFactory {
    
    
    public View create(Element elem) {
      
      String name = elem.getName();

      
      if ("prop".equals(name)) {
        View result = new PropertyView(elem);
        propViews.add(result);
        return result;
        
      }
      
      
      if ("name".equals(name)||"i18n".equals(name)) {
        return new I18NView(elem);
      }
        
      
      View result = super.create(elem);

      
      if ("table".equals(elem.getName())) {
        tableViews.add(result);
      }
      return result;
    }
  
  } 
  
  
  private abstract class MyView extends View {
  
    
    private Font font = null;
    
    
    private Color foreground = null;
    
    
    private Dimension2D preferredSpan = null;
    
    
    MyView(Element elem) {
      super(elem);
    }
    
    public int viewToModel(float arg0, float arg1, Shape arg2, Bias[] arg3) {
      throw new RuntimeException("viewToModel() is not supported");
    }
    
    public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
      throw new RuntimeException("modelToView() is not supported");
    }
    
    public int getBreakWeight(int axis, float pos, float len) {
      
      if (axis==Y_AXIS) return BadBreakWeight;
      
      if (len > getPreferredSpan(X_AXIS)) {
        return GoodBreakWeight;
      }
      return BadBreakWeight;
    }  
    
    public View breakView(int axis, int offset, float pos, float len) {
      return this;
    }
    
    
    public float getPreferredSpan(int axis) {
      
      if (preferredSpan==null) {
        preferredSpan = getPreferredSpan();
      }
      return (float)(axis==X_AXIS ? preferredSpan.getWidth() : preferredSpan.getHeight());
    }
    
    
    public float getMaximumSpan(int axis) {
      return getPreferredSpan(axis);
    }

    
    public float getAlignment(int axis) {
      
      if (X_AXIS==axis) 
        return super.getAlignment(axis);
      
      float height = (float)getPreferredSpan().getHeight();
      
      LineMetrics lm = getFont().getLineMetrics("", context);
      float h = lm.getHeight();
      float d = lm.getDescent();
      return (h-d)/height;
    }
    
        protected abstract Dimension2D getPreferredSpan();

    
    protected Font getFont() {
      
      
      
      
      if (font==null) {
        font = doc.getFont(doc.getStyleSheet().getViewAttributes(this));
      }
      return font;
    }
    
    
    protected Color getForeground() {
  
      
      
      
      
      if (foreground==null) 
        
        foreground = doc.getForeground(doc.getStyleSheet().getViewAttributes(this));
      
      return foreground;
    }
    
    
    protected void invalidate() {
      
      preferredSpan = null;
      
      super.preferenceChanged(this,true,true);
    }
    
    
    public ViewFactory getViewFactory() {
      return factory;
    }

  } 

  
  private class RootView extends MyView {

    
    private View view;
    
    
    private float width, height;

    
    RootView(View view) {
      
      
      super(null);

      
      this.view = view;
      
      try {
        view.setParent(this);
      } catch (Throwable t) {
      }
      
      
    }

    
    public AttributeSet getAttributes() {
      return null;
    }

    
    public void paint(Graphics g, Shape allocation) {
      try {
        view.paint(g, allocation);
      } catch (Throwable t) {
      }
    }

    
    public Document getDocument() {
      return doc;
    }

        
    public void setSize(float wIdth, float heIght) {
      
      width = wIdth;
      height = heIght;
      
      try {
        view.setSize(width, height);
      } catch (Throwable t) {
      }
      
    }

    
    protected Dimension2D getPreferredSpan() {
      return new Dimension2d(
        (int)view.getPreferredSpan(X_AXIS),
        (int)view.getPreferredSpan(Y_AXIS)
      );
    }

  } 

    private class I18NView extends MyView {
    
    
    private String txt = "?";
    
        private I18NView(Element elem) {
      super(elem);
      
      Object o = elem.getAttributes().getAttribute("tag");
      if (o!=null) txt = Gedcom.getName(o.toString());
      else {
        o = elem.getAttributes().getAttribute("entity");
        if (o!=null) txt = Gedcom.getName(o.toString());
      }
      
    }
    
    public void paint(Graphics g, Shape allocation) {
      Rectangle r = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
      g.setFont(getFont());
      g.setColor(getForeground());
      PropertyRenderer.DEFAULT_RENDERER.renderImpl((Graphics2D)g,r,txt,Collections.EMPTY_MAP);
    }
    
    protected Dimension2D getPreferredSpan() {
      return PropertyRenderer.DEFAULT_RENDERER.getSizeImpl(getFont(), context, null, txt, Collections.EMPTY_MAP, dpi);
    }
  } 

  
  private class PropertyView extends MyView {
    
    
    
    
    private TagPath path = null;
    
    
    private Property cachedProperty = null;
    
    
    private Map attributes;
    
    
    private int min, max;
    
    
    private boolean isValid = false;
    
    
    PropertyView(Element elem) {
      super(elem);

      
      attributes = new HashMap();
      
      for (Enumeration as = elem.getAttributes().getAttributeNames(); as.hasMoreElements(); ) {
        String key = as.nextElement().toString();
        attributes.put(key, elem.getAttributes().getAttribute(key));
      }
      
      
      Object p = elem.getAttributes().getAttribute("path");
      if (p!=null) try {
        path = new TagPath(p.toString());
      } catch (IllegalArgumentException e) {
        
      }
      
      
      min = getAttribute("min", 1, 100, 1);
      max = getAttribute("max", 1, 100, 100);
      
      
    }
    
        private int getAttribute(String key, int min, int max, int def) {
      
      Object val = attributes.get(key);
      if (val!=null) try {
        return Math.max(min, Math.min(max, Integer.parseInt(val.toString())));
      } catch (NumberFormatException e) {
      }
      
      return def;
    }
    
    
    private Property getProperty() {
      
      if (!isValid&&entity!=null&path!=null) {
        cachedProperty = entity.getProperty(path);
        
        isValid = true;
      }      
      
      return cachedProperty;
    }
    
    
    private PropertyRenderer getRenderer(Property prop) {
      
      
      
      
      
      
      
      PropertyRenderer result = PropertyRenderer.get(path, prop);

      
      if (prop==null&&!result.isNullRenderer()) 
        return null;
      
      
      return result;
    }
    
    
    public void paint(Graphics g, Shape allocation) {
      Graphics2D graphics = (Graphics2D)g;
      
      Property property = getProperty();
      PropertyRenderer renderer = getRenderer(property);
      
      if (renderer==null) return;
      
      g.setColor(super.getForeground());
      g.setFont(super.getFont());
      Rectangle r = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
      
      if (isDebug) 
        graphics.draw(r);
      
      Shape old = graphics.getClip();
      graphics.clip(r);
      renderer.render(graphics, r, property, attributes, dpi);
      g.setClip(old);
      
    }
    
    protected Dimension2D getPreferredSpan() {
      
      Property property = getProperty();
      PropertyRenderer renderer = getRenderer(property);
      
      if (renderer==null)
        return new Dimension(0,0);
      
      Dimension2D d = renderer.getSize(getFont(), context, property, attributes, dpi);
      
      d = new Dimension2d(Math.min(d.getWidth(), root.width*max/100), d.getHeight());
      return d;
    }
    
    public float getMinimumSpan(int axis) {
      float pref = getPreferredSpan(axis);
      if (axis==Y_AXIS) return pref;
      return Math.min(pref, root.width*min/100);
    }
    
    protected void invalidate() {
      
      
      isValid = false;
      super.invalidate();
    }
    
  } 
 
} 
