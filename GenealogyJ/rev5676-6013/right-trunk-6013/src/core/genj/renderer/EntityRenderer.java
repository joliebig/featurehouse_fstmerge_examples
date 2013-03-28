
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Dimension2d;
import genj.util.EnvironmentChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
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

  private final static Logger LOG = Logger.getLogger("genj.renderer");
  
  
  
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
  
  
  private Entity entity;
  
  
  private List<PropertyView> propViews = new ArrayList<PropertyView>(16);
  
  
  private List<View> tableViews = new ArrayList<View>(4);
  
  
  private boolean isDebug = false;
  
  
  private Graphics2D graphics;
  
  private Font plain,bold,italic;
  
    
  public EntityRenderer(Blueprint bp) {
    
    
    StringBuffer html = new StringBuffer();
    html.append("<html<body>");
    html.append(bp.getHTML());
    html.append("</body></html>");

    
    try {
      
      
      
      
      
      
      
      
      
      
      
      
      MyHTMLReader reader = new MyHTMLReader(doc);
      
      
      new MyDocumentParser(DTD.getDTD("html32")).parse(new StringReader(html.toString()), reader, false);
      
      
      reader.flush();      
      
    } catch (Throwable t) {
      Logger.getLogger("genj.renderer").log(Level.WARNING, "can't parse blueprint "+bp, t);
    }

    
    root = new RootView(factory.create(doc.getDefaultRootElement()));

    
  }
  
  
  public void render(Graphics g, Entity e, Rectangle r) {

    
    entity = e;
    graphics = (Graphics2D)g;
    
    
    
    Font font = g.getFont();
    if (!EnvironmentChecker.isMac()) {
      float factor = DPI.get(graphics).vertical()/72F; 
      font = font.deriveFont(factor*font.getSize2D());
    }
    this.plain = font;
    this.bold = font.deriveFont(Font.BOLD);
    this.italic = font.deriveFont(Font.ITALIC);

    try {
      
      
      for (PropertyView pv : propViews) 
        pv.invalidate();
      
      
      for (View tv : tableViews) {
        
        tv.replace(0,0,null);
      }
      
      
      root.setSize((float)r.getWidth(),(float)r.getHeight());
      
      
      Rectangle oc = g.getClipBounds();
      g.clipRect(r.x,r.y,r.width,r.height);
      try {
        root.paint(g, r);
      } finally {
        g.setClip(oc.x,oc.y,oc.width,oc.height);
      }

    } catch (Throwable t) {
      LOG.log(Level.WARNING, "can't render", t);
    }
    
  }
  
  
  public void setDebug(boolean set) {
    isDebug = set;
  }

    
  private class MyHTMLDocument extends HTMLDocument {
    
    
    public Font getFont(AttributeSet attr) {
      
      Font result = plain;
      if (StyleConstants.isBold(attr)) 
        result = bold;
      else if (StyleConstants.isItalic(attr))
        result = italic;
      return result;
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
        PropertyView result = new PropertyView(elem);
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
    
    @Override
    public float getMinimumSpan(int axis) {
      return getPreferredSpan(axis);
    }
    
    
    public float getMaximumSpan(int axis) {
      return getPreferredSpan(axis);
    }

    
    public float getAlignment(int axis) {
      
      if (X_AXIS==axis) 
        return super.getAlignment(axis);
      
      float height = (float)getPreferredSpan().getHeight();
      
      FontMetrics fm = getGraphics().getFontMetrics();
      float h = fm.getHeight();
      float d = fm.getDescent();
      return (h-d)/height;
    }
    
    @Override
    public Graphics getGraphics() {
      graphics.setFont(getFont());
      return graphics;
    }
    
    
    protected Color getForeground() {
  
      
      
      
      
      if (foreground==null) 
        
        foreground = doc.getForeground(doc.getStyleSheet().getViewAttributes(this));
      
      return foreground;
    }
    
    
    protected Font getFont() {
      
      
      
      
      if (font==null) {
        font = doc.getFont(doc.getStyleSheet().getViewAttributes(this));
      }
      return font;
    }
    
    
    protected abstract Dimension2D getPreferredSpan();

    
    protected void invalidate() {
      
      preferredSpan = null;
      font = null;
      
      super.preferenceChanged(this,true,true);
    }
    
    
    public ViewFactory getViewFactory() {
      return factory;
    }

  } 

  
  private class RootView extends View {
  
    
    private View view;
    
    
    private float width, height;
  
    
    RootView(View view) {
      
      
      super(null);
  
      
      this.view = view;
      view.setParent(this);
      
      
    }
    
    @Override
    public float getPreferredSpan(int axis) {
      throw new RuntimeException("getPreferredSpan() is not supported");
    }
    
    
    public int viewToModel(float arg0, float arg1, Shape arg2, Bias[] arg3) {
      throw new RuntimeException("viewToModel() is not supported");
    }
    
    public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
      throw new RuntimeException("modelToView() is not supported");
    }
    
    
    public AttributeSet getAttributes() {
      return null;
    }
  
    
    public void paint(Graphics g, Shape allocation) {
      view.paint(g, allocation);
    }
  
    
    public Document getDocument() {
      return doc;
    }
    
    @Override
    public Graphics getGraphics() {
      return graphics;
    }
    
        
    public void setSize(float wIdth, float heIght) {
      
      width = wIdth;
      height = heIght;
      
      try {
        view.setSize(width, height);
      } catch (Throwable t) {
      }
      
    }

    
    public ViewFactory getViewFactory() {
      return factory;
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
      PropertyRenderer.DEFAULT.renderImpl((Graphics2D)g,r,txt,new HashMap<String, String>());
    }
    
    protected Dimension2D getPreferredSpan() {
      return PropertyRenderer.DEFAULT.getSizeImpl(null, txt, new HashMap<String, String>(), (Graphics2D)getGraphics());
    }
  } 

  
  private class PropertyView extends MyView {
    
    
    
    
    private List<TagPath> paths = new ArrayList<TagPath>();
    
    
    private Property cachedProperty = null;
    
    
    private Map<String,String> attributes;
    
    
    private int min, max;
    
    
    PropertyView(Element elem) {
      super(elem);

      
      attributes = new HashMap<String, String>();
      
      for (Enumeration<?> as = elem.getAttributes().getAttributeNames(); as.hasMoreElements(); ) {
        Object key = as.nextElement();
        if (key instanceof String)
          attributes.put((String)key, (String)elem.getAttributes().getAttribute(key));
      }
      
      
      Object p = elem.getAttributes().getAttribute("path");
      if (p!=null) for (String path : p.toString().split(",")) try {
        paths.add(new TagPath(path));
      } catch (IllegalArgumentException e) {
        if (LOG.isLoggable(Level.FINER))
          LOG.log(Level.FINER, "got wrong path "+path);
      }
      
      
      min = getAttribute("min", 1, 100, 1);
      max = getAttribute("max", 1, 100, 100);
      
      
    }
    
    
    private int getAttribute(String key, int min, int max, int def) {
      
      String val = attributes.get(key);
      if (val!=null) try {
        return Math.max(min, Math.min(max, Integer.parseInt(val)));
      } catch (NumberFormatException e) {
      }
      
      return def;
    }
    
    
    private Property getProperty() {
      
      if (cachedProperty!=null)
        return cachedProperty;
      
      if (entity==null||paths.isEmpty())
        return null;

      Property result = null;
      
      for (TagPath path : paths) {
        result = entity.getProperty(path);
        if (result!=null)
          break;
      }
      
      if (paths.size()==1)
        cachedProperty = result;
      
      return result;
    }
    
    
    private PropertyRenderer getRenderer(Property prop) {
      
      
      
      
      
      
      
      PropertyRenderer result = PropertyRendererFactory.DEFAULT.getRenderer(paths.get(0), prop);

      
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
      renderer.render(graphics, r, property, attributes);
      g.setClip(old);
      
    }
    
    protected Dimension2D getPreferredSpan() {
      
      Property property = getProperty();
      PropertyRenderer renderer = getRenderer(property);
      
      if (renderer==null)
        return new Dimension(0,0);
      
      Dimension2D d = renderer.getSize(property, attributes, (Graphics2D)getGraphics());
      
      d = new Dimension2d(Math.min(d.getWidth(), root.width*max/100), d.getHeight());
      return d;
    }
    
    public float getMinimumSpan(int axis) {
      float pref = getPreferredSpan(axis);
      if (axis==Y_AXIS) return pref;
      return Math.min(pref, root.width*min/100);
    }
    
    protected void invalidate() {
      
      
      cachedProperty = null;
      
      super.invalidate();
    }
    
  } 

} 
