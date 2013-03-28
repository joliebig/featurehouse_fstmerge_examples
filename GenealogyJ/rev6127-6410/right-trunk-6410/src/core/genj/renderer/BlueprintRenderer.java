
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.util.Dimension2d;
import genj.util.EnvironmentChecker;
import genj.util.swing.ImageIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
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


public class BlueprintRenderer {

  private final static ImageIcon BROKEN = new ImageIcon(BlueprintEditor.class, "Broken.png");
  
  private final static Logger LOG = Logger.getLogger("genj.renderer");
  
  public static final String HINT_KEY_TXT = "txt";
  public static final String HINT_KEY_IMG = "img";
  public static final String HINT_KEY_SHORT = "short";

  public static final String HINT_VALUE_TRUE = "yes";
  public static final String HINT_VALUE_FALSE = "no";
  
  private final static String STARS = "*****";
  
  private final static int IMAGE_GAP = 4;
  
  
  
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
  
  
  private List<MyView> volatileViews = new ArrayList<MyView>(64);
  
  
  private List<View> tableViews = new ArrayList<View>(4);
  
  
  private boolean isDebug = false;
  
  
  private Graphics2D graphics;
  
  private Font plain,bold,italic;
  
    
  public BlueprintRenderer(Blueprint bp) {
    
    
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
      
      
      for (MyView pv : volatileViews) 
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
  
  
  protected Property getProperty(Entity entity, TagPath path) {
    return entity.getProperty(path);
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
        volatileViews.add(result);
        return result;
        
      }
      
      
      if ("name".equals(name)||"i18n".equals(name)) {
        return new I18NView(elem);
      }
      
      
      if ("media".equals(name)) {
        MediaView result = new MediaView(elem);
        volatileViews.add(result);
        return result;
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

    
    private int max = 0;
    
    
    MyView(Element elem) {
      super(elem);
      
      
      try {
        max = Integer.parseInt((String)elem.getAttributes().getAttribute("max"));
      } catch (Throwable t) {
      }
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
        
        if (max>0) {
          double maxWidth = root.width*max/100;
          if (preferredSpan.getWidth()>maxWidth)
            preferredSpan = new Dimension2d(maxWidth, preferredSpan.getHeight() * maxWidth/preferredSpan.getWidth());
        }        
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

    protected void render(String txt, Graphics2D g, Rectangle r) {
      
      
      if (txt.length()==0)
        return;
      
      
      TextLayout layout = new TextLayout(txt, g.getFont(), g.getFontRenderContext());
      
      
      layout.draw(g, (float)r.getX(), (float)r.getY()+layout.getAscent());
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
        LOG.log(Level.FINE, "unexpected", t);
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
      render(txt,(Graphics2D)g,r);
    }
    
    protected Dimension2D getPreferredSpan() {
      FontMetrics fm = graphics.getFontMetrics(getFont());
      return new Dimension(
        fm.stringWidth(txt),
        fm.getAscent() + fm.getDescent()
      );
    }
  } 

  
  private class MediaView extends MyView {
    
    private TagPath path2root = null;

    
    MediaView(Element elem) {
      super(elem);

      Object p = elem.getAttributes().getAttribute("path");
      if (p!=null) try {
        path2root = new TagPath((String)p);
      } catch (IllegalArgumentException e) {
        if (LOG.isLoggable(Level.FINER))
          LOG.log(Level.FINER, "got wrong path "+p);
      }
    }
    
    private Property getRoot() {
      Property result = null;
      if (path2root!=null)
        result = entity.getProperty(path2root);
      return result !=null ? result : entity;
    }
    
    @Override
    protected Dimension2D getPreferredSpan() {
      Dimension2D size = MediaRenderer.getSize(getRoot(), graphics);
      if (isDebug && size.getWidth()==0&&size.getHeight()==0)
        return BROKEN.getSizeInPoints(DPI.get(graphics));
      return size;
    }

    @Override
    public void paint(Graphics g, Shape allocation) {
      
      Rectangle r = allocation.getBounds();

      if (isDebug) {
        Dimension2D size = MediaRenderer.getSize(getRoot(), graphics);
        if (size.getWidth()==0&&size.getHeight()==0) {
          BROKEN.paintIcon(g, r.x, r.y);
          return;
        }
      }
      MediaRenderer.render(g, r, getRoot());
    }
    
  }
  
  
  private class PropertyView extends MyView {
    
    
    private Map<String,String> attributes;
    private TagPath path = null;
    
    
    private Property cachedProperty = null;
    private Dimension2D cachedSize = null;
    
    
    PropertyView(Element elem) {
      super(elem);

      
      attributes = new HashMap<String, String>();
      
      for (Enumeration<?> as = elem.getAttributes().getAttributeNames(); as.hasMoreElements(); ) {
        Object key = as.nextElement();
        if (key instanceof String)
          attributes.put((String)key, (String)elem.getAttributes().getAttribute(key));
      }
      
      
      Object p = elem.getAttributes().getAttribute("path");
      if (p!=null) try {
        path = new TagPath((String)p);
      } catch (IllegalArgumentException e) {
        if (LOG.isLoggable(Level.FINER))
          LOG.log(Level.FINER, "got wrong path "+p);
      }
      
      
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
      if (entity==null||path==null)
        return null;
      cachedProperty = BlueprintRenderer.this.getProperty(entity, path);
      return cachedProperty;
    }
    
    
    public void paint(Graphics g, Shape allocation) {
      
      Property prop = getProperty();
      if (prop==null)
        return;
      
      Graphics2D graphics = (Graphics2D)g;
      
      
      g.setColor(super.getForeground());
      g.setFont(super.getFont());
      
      Rectangle r = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
      
      
      if (isDebug) 
        graphics.draw(r);
      
      
      Shape old = graphics.getClip();
      graphics.clip(r);
      render(prop, graphics, r);
      g.setClip(old);
      
      
    }
    
    private void render(Property prop, Graphics2D g, Rectangle r) {
      if (prop instanceof MultiLineProperty) {
        render((MultiLineProperty)prop, g, r);
        return;
      }
      if (prop instanceof PropertyFile||prop instanceof PropertyBlob) {
        MediaRenderer.render(g, r, prop);
        return;
      }
      
      if (HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_IMG))) 
        render(prop instanceof PropertyDate ? prop.getParent().getImage(false) : prop.getImage(false), g, r);
      
      if (!HINT_VALUE_FALSE.equals(attributes.get(HINT_KEY_TXT))) 
        render(getText(prop), g, r);
    }
    
    private void render(MultiLineProperty mle, Graphics2D g, Rectangle r) {
      
      MultiLineProperty.Iterator line = mle.getLineIterator();
      
      
      Graphics2D graphics = (Graphics2D)g;
      Font font = g.getFont();
      FontRenderContext context = graphics.getFontRenderContext();

      float 
        x = (float)r.getX(),
        y = (float)r.getY();
      
      do {

        
        String txt = line.getValue();
        LineMetrics lm = font.getLineMetrics(txt, context);
        y += lm.getHeight();
        
        
        graphics.drawString(txt, x, y - lm.getDescent());
        
        
        if (y>r.getMaxY()) 
          break;
        
      } while (line.next());
      
      
    }
    
    private void render(ImageIcon img, Graphics2D g, Rectangle bounds) {
      
      
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
        
    
    private String getText(Property prop) {
      if (prop instanceof Entity)
        return getText((Entity)prop);
      if (prop.isPrivate())
        return STARS;
      if (prop instanceof PropertyPlace)
        return getText((PropertyPlace)prop);
      if (prop instanceof PropertySex)
        return getText((PropertySex)prop);
      
      return prop.getDisplayValue();
    }
    
    private String getText(Entity entity) {
      return entity.getId();
    }
    
    private String getText(PropertySex sex) {
      
      if (!attributes.containsKey(HINT_KEY_TXT))
        attributes.put(HINT_KEY_TXT, HINT_VALUE_FALSE);
      if (!attributes.containsKey(HINT_KEY_IMG))
        attributes.put(HINT_KEY_IMG, HINT_VALUE_TRUE);
        
      String result = sex.getDisplayValue();
      if (result.length()>0 && HINT_VALUE_TRUE.equals(attributes.get(HINT_KEY_SHORT)))
        result = result.substring(0,1);
      return result;
    }
    
    private String getText(PropertyPlace place) {
      return place.format(attributes.get("format"));
    }
    
    
    protected Dimension2D getPreferredSpan() {
      
      if (cachedSize!=null)
        return cachedSize;
      
      cachedSize = getSize();
      return cachedSize;
    }
    
    private Dimension2D getSize() {
      Property prop = getProperty();
      if (prop==null)
        return new Dimension();
      if (prop instanceof MultiLineProperty)
        return getSize((MultiLineProperty)prop);
      if (prop instanceof PropertyFile || prop instanceof PropertyBlob)
        return MediaRenderer.getSize(prop, getGraphics());
      return getSize(prop);
    }
    
    private Dimension2D getSize(Property prop) {
      
      String txt = getText(prop);
      
      double 
        w = 0,
        h = 0;
      
      
      graphics.setFont(super.getFont());
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
    
    private Dimension2D getSize(MultiLineProperty mle) {
      
      
      graphics.setFont(super.getFont());
      FontMetrics fm = graphics.getFontMetrics();
      int lines = 0;
      double width = 0;
      double height = 0;
      MultiLineProperty.Iterator line = mle.getLineIterator();
      do {
        lines++;
        width = Math.max(width, fm.stringWidth(line.getValue()));
        height += fm.getHeight();
      } while (line.next());
      
      
      return new Dimension2d(width, height);
    }
    
    
    protected void invalidate() {
      
      
      cachedProperty = null;
      cachedSize = null;
      super.invalidate();
    }
    
  } 

} 
