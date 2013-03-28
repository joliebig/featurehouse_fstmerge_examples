
package genj.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class NestedBlockLayout implements LayoutManager2, Cloneable {
  
  private final static SAXException DONE = new SAXException("");
  private final static SAXParser PARSER = getSaxParser();
  
  private final static SAXParser getSaxParser() {
    try {
      return SAXParserFactory.newInstance().newSAXParser();
    } catch (Throwable t) {
      Logger.getLogger("genj.util.swing").log(Level.SEVERE, "Can't initialize SAX parser", t);
      throw new Error("Can't initialize SAX parser", t);
    }
  }
  
  private final static Logger LOG = Logger.getLogger("genj.util");

  
  private boolean invalidated = true;
  
  
  private Block root;
  
  
  private int padding = 1;
  
  
  private NestedBlockLayout(Block root) {
    this.root = root;
  }

  
  public NestedBlockLayout(String descriptor) {
    try {
      init(new StringReader(descriptor));
    } catch (IOException e) {
      
    }
  }

  
  public NestedBlockLayout(Reader descriptor) throws IOException {
    init(descriptor);
  }
  
  
  public NestedBlockLayout(InputStream descriptor) throws IOException {
    init(new InputStreamReader(descriptor));
  }
  
  
  public Collection<Cell> getCells() {
    return root.getCells(new ArrayList<Cell>(10));
  }
  
  
  private void init(Reader descriptor) throws IOException {
    
    
    try {
	    PARSER.parse(new InputSource(descriptor), new DescriptorHandler());
    } catch (SAXException sax) {
      if (DONE==sax) {
        return;
      }
      throw new RuntimeException(sax);
    } catch (IOException ioe) {
      throw (IOException)ioe;
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
    
    
  }
  
  
  private class DescriptorHandler extends DefaultHandler {
    
    private Stack<Block> stack = new Stack<Block>();
    
    public InputSource resolveEntity(String publicId, String systemId) {
      
      
      
      throw new IllegalArgumentException("Request for resolveEntity "+publicId+"/"+systemId+" not allowed in layout descriptor");
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      
      boolean startsWithSpace = Character.isWhitespace(ch[start]);
      boolean endsWithSpace = Character.isWhitespace(ch[start+length-1]);
      
      
      while (length>0 && Character.isWhitespace(ch[start])) { start++; length--; }
      while (length>0 && Character.isWhitespace(ch[start+length-1])) length--;
      if (length==0)
        return;
      
      
      if (startsWithSpace) { start--; length++; }
      if (endsWithSpace) { length++; }
      String s = new String(ch,start,length);
            
      Block parent = (Block)stack.peek();
      parent.add(new Cell(s));
    }
    
    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws org.xml.sax.SAXException {
      
      Block block = getBlock(qName, attributes);
      
      if (stack.isEmpty()) {
        root = block;
      } else {
        Block parent = (Block)stack.peek();
	      parent.add(block);
      }
      
      stack.add(block);
      
    }
    
    private Block getBlock(String element, Attributes attrs) {
      
      if ("row".equals(element)) 
        return new Row();
      
      if ("col".equals(element))
        return new Column();
      
      return new Cell(element, attrs, padding);
    }
    
    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
      
      
      if (stack==null||stack.size()==0)
        throw new SAXException("unexpected /element");

      
      Block block = (Block)stack.pop();
      
      
      if (stack.isEmpty())
        throw DONE;
    }
    
  };

  
  private static abstract class Block implements Cloneable {
    
    
    Dimension preferred;

    
    Point2D.Double weight;
    Point grow;
    
    
    ArrayList<Block> subs = new ArrayList<Block>(16);
    
    
    protected Object clone() {
      try {
        Block clone = (Block)super.clone();
        
        clone.subs = new ArrayList<Block>(subs.size());
        for (int i=0;i<subs.size();i++)
          clone.subs.add( (Block)subs.get(i).clone() );
        return clone;
      } catch (CloneNotSupportedException cnse) {
        throw new Error();
      }
    }
    
    
    boolean remove(Component component) {

      
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        if (sub.remove(component)) {
          invalidate(false);
          return true;
        }
      }
      
      
      return false;
      
    }
    
    
    Block add(Block block) {
      subs.add(block);
      invalidate(false);
      return block;
    }
    
    
    void invalidate(boolean recurse) {
      
      
      preferred = null;
      weight = null;
      grow = null;

      
      if (recurse) for (int i=0;i<subs.size();i++) {
        ((Block)subs.get(i)).invalidate(true);
      }
    }
    
    
    abstract Point2D weight();
    
    
    Point grow() {
      
      
      if (grow!=null)
        return grow;
      
      
      grow = new Point();
      for (Block block : subs) {
        Point sub = block.grow();
        if (sub.x==1)
          grow.x=1;
        if (sub.y==1)
          grow.y=1;
      }      
      
      
      return grow;
    }
    
    
    abstract Dimension preferred();
      
    
    abstract void layout(Rectangle in);
    
    
    Collection<Cell> getCells(Collection<Cell> collect) {
      for (int i=0;i<subs.size();i++) 
        ((Block)subs.get(i)).getCells(collect);
      return collect;
    }
    
    
    Cell getCell(String element) {
      
      Cell result = null;
      for (int i=0;result==null&&i<subs.size();i++) {

        
        Block sub = (Block)subs.get(i);
        result = sub.getCell(element);
        
        
      }
      return result;
    }

  } 
  
  
  private static class Row extends Block {

    
    Block add(Block sub) {
      if (sub instanceof Row)
        throw new IllegalArgumentException("row can't contain row");
      super.add(sub);
      return sub;
    }
    
    
    Dimension preferred() {
      
      if (preferred!=null)
        return preferred;
    
      
      preferred = new Dimension();
      for (int i=0;i<subs.size();i++) {
        Dimension sub = ((Block)subs.get(i)).preferred();
        preferred.width += sub.width;
        preferred.height = Math.max(preferred.height, sub.height);
      }
    
      
      return preferred;
    }
    
    
    @Override
    Point2D weight() {
      
      
      if (weight!=null)
        return weight;
      
      
      weight = new Point2D.Double();
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        weight.x += sub.weight().getX();
        weight.y = Math.max(weight.y, sub.weight().getY());
      }      
      
      
      return weight;
    }
    
    
    @Override
    void layout(Rectangle in) {
      
      
      double weight = 0;
      int grow = 0;
      int spare = in.width;
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        spare -= sub.preferred().width;
        weight += sub.weight().getX();
        grow += sub.grow().x;
      }
      double weightFactor = weight>0 ? spare/weight : 0;
      int growFactor = weightFactor==0 && grow>0 ? spare/grow : 0;
      
      
      Rectangle avail = new Rectangle(in.x, in.y, 0, 0);
      for (int i=0;i<subs.size();i++) {
        
        Block sub = (Block)subs.get(i);
        
        avail.width = sub.preferred().width + (int)(sub.weight().getX() * weightFactor) + (sub.grow().x*growFactor);
        avail.height = in.height;

        sub.layout(avail);
  
        avail.x += avail.width;
      }
      
    }
    
  } 
  
  
  private static class Column extends Block {
    
    
    Block add(Block sub) {
      if (sub instanceof Column)
        throw new IllegalArgumentException("column can't contain column");
      super.add(sub);
      return sub;
    }

    
    Dimension preferred() {
      
      if (preferred!=null)
        return preferred;
    
      
      preferred = new Dimension();
      for (int i=0;i<subs.size();i++) {
        Dimension sub = ((Block)subs.get(i)).preferred();
        preferred.width = Math.max(preferred.width, sub.width);
        preferred.height += sub.height;
      }
    
      
      return preferred;
    }
    
    
    Point2D weight() {
      
      
      if (weight!=null)
        return weight;
      
      
      weight = new Point2D.Double();
      for (int i=0;i<subs.size();i++) {
        Point2D sub = ((Block)subs.get(i)).weight();
        weight.x = Math.max(weight.x, sub.getX());
        weight.y += sub.getY();
      }      
      
      
      return weight;
    }
    
    
    void layout(Rectangle in) {
      
      
      double weight = 0;
      int spare = in.height;
      int grow = 0;
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        spare -= sub.preferred().height;
        weight += sub.weight().getY();
        grow += sub.grow().y;
      }
      double weightFactor = weight>0 ? spare/weight : 0;
      int growFactor = weightFactor==0 && grow>0 ? spare/grow : 0;
      
      
      Rectangle avail = new Rectangle(in.x, in.y, 0, 0);
      for (int i=0;i<subs.size();i++) {
        
        Block sub = (Block)subs.get(i);
        
        avail.width = in.width;
        avail.height = sub.preferred().height + (int)(sub.weight().getY() * weightFactor) + (sub.grow().y*growFactor);
        
        sub.layout(avail);
  
        avail.y += avail.height;
      }
      
    }
    
  } 
  
  
  public static class Cell extends Block {
    
    
    private String element;
    
    
    private Map<String,String> attrs = new HashMap<String, String>();
    
    
    private Component component;
    
    
    private int cellPadding;
    
    
    private Point2D.Double cellWeight = new Point2D.Double();
    
    
    private Point2D.Double cellAlign = new Point2D.Double(0.5,0.5);

    
    private Cell(String text) {
      this.element = "text";
      attrs.put("value", text);
    }
    
    
    private Cell(String element, Attributes attributes, int padding) {
      
      
      this.element = element;
      this.cellPadding = padding;
      
      for (int i=0,j=attributes.getLength();i<j;i++) 
        attrs.put(attributes.getQName(i), attributes.getValue(i));
      
      
      String wx = getAttribute("wx");
      if (wx!=null)
        cellWeight.x = Float.parseFloat(wx);
      String wy = getAttribute("wy");
      if (wy!=null)
        cellWeight.y = Float.parseFloat(wy);
      
      
      String ax = getAttribute("ax");
      if (ax!=null)
        cellAlign.x = Float.parseFloat(ax);
      String ay = getAttribute("ay");
      if (ay!=null)
        cellAlign.y = Float.parseFloat(ay);
      
      
      grow = new Point();
      String gx = getAttribute("gx");
      if (gx!=null)
        grow.x = Integer.parseInt(gx)>0 ? 1 : 0;
      else if (cellWeight.getX()>0)
        grow.x = 1;
      String gy = getAttribute("gy");
      if (gy!=null)
        grow.y = Integer.parseInt(gy)>0 ? 1 : 0;
      else if (cellWeight.getY()>0)
        grow.y = 1;

      
    }
    
    
    protected Object clone()  {
      Cell clone = (Cell)super.clone();
      clone.component = null;
      return clone;
    }
    
    @Override
    void invalidate(boolean arg0) {
      
      preferred = null;
    }
    
    
    void setContent(Component component) {
      this.component = component;
    }
    
    
    public Collection<NestedBlockLayout> getNestedLayouts() {
      ArrayList<NestedBlockLayout> result = new ArrayList<NestedBlockLayout>(subs.size());
      for (int i = 0; i < subs.size(); i++) {
        result.add(new NestedBlockLayout((Block)subs.get(i)));
      }
      return result;
    }
    
    
    public String getElement() {
      return element;
    }
    
    
    public boolean isAttribute(String attr) {
      return attrs.containsKey(attr);
    }
    
    
    public String getAttribute(String attr) {
      return (String)attrs.get(attr);
    }
    
    
    boolean remove(Component component) {
      if (this.component==component) {
        this.component = null;
        invalidate(false);
        return true;
      }
      return false;
    }
    
    
    Dimension preferred() {
      
      if (preferred!=null)
        return preferred;
      
      if (component==null||!component.isVisible())
        preferred = new Dimension();
      else {
	      preferred = new Dimension(component.getPreferredSize());
	      preferred.width += cellPadding*2;
	      preferred.height += cellPadding*2;
      }
	    return preferred;
    }
    
    
    @Override
    Point2D weight() {
      return component==null ? new Point2D.Double() : cellWeight;
    }
    
    
    void layout(Rectangle in) {
      
      if (component==null)
        return;
      
      
      Rectangle avail = new Rectangle(in.x+cellPadding, in.y+cellPadding, in.width-cellPadding*2, in.height-cellPadding*2);
      
      
      Dimension pref = preferred();
      Dimension max = component.getMaximumSize();
      if (grow.x!=0) 
        max.width = avail.width;
      else if (cellWeight.x==0) 
        max.width = pref.width;
        
      if (grow.y!=0) 
        max.height = avail.height;
      else if (cellWeight.y==0)
        max.height = pref.height;
      
      
      int extraX = avail.width-max.width;
      if (extraX>0) {
        avail.x += extraX * cellAlign.x;
        avail.width = max.width;
      }
      
      int extraY = avail.height-max.height;
      if (extraY>0) {
        avail.y += extraY * cellAlign.y;
        avail.height = max.height;
      }

      
      component.setBounds(avail);
    }
    
    
    Cell getCell(String elem) {
      return ( (elem==null&&component==null) || element.equals(elem)) ? this : null;
    }
    
    
    Collection<Cell> getCells(Collection<Cell> collect) {
      collect.add(this);
      return collect;
    }

  } 
  
  
  public void addLayoutComponent(Component comp, Object key) {

    
    if (key instanceof Cell) {
      ((Cell)key).setContent(comp);
      return;
    }
    
    
    Cell cell = root.getCell(key!=null ? key.toString() : null);
    if (cell!=null) {
      cell.setContent(comp);
      return;
    }
  
    
    if (key==null)
      throw new IllegalArgumentException("no available descriptor element - element qualifier required");
    throw new IllegalArgumentException("element qualifier doesn't match any descriptor element");

    
  }

  
  public void addLayoutComponent(String element, Component comp) {
    addLayoutComponent(comp, element);
  }

  
  public void removeLayoutComponent(Component comp) {
    root.remove(comp);
  }

  
  public Dimension maximumLayoutSize(Container target) {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  
  public float getLayoutAlignmentX(Container target) {
    return 0;
  }

  
  public float getLayoutAlignmentY(Container target) {
    return 0;
  }

  
  public void invalidateLayout(Container target) {
    if (!invalidated) {
      root.invalidate(true);
      invalidated = true;
    }
  }

  
  public Dimension preferredLayoutSize(Container parent) {
    invalidated = false;
    return root.preferred();
  }
  
  
  public void layoutContainer(Container parent) {
    
    
    Insets insets = parent.getInsets();
    Rectangle in = new Rectangle(
      insets.left,
      insets.top,
      parent.getWidth()-insets.left-insets.right,
      parent.getHeight()-insets.top-insets.bottom
    );
    
    root.layout(in);
    
    invalidated = false;
  }
  
  
  public NestedBlockLayout copy() {
    try {
      NestedBlockLayout clone = (NestedBlockLayout)super.clone();
      clone.root = (Block)clone.root.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new Error(e);
    }
  }

} 