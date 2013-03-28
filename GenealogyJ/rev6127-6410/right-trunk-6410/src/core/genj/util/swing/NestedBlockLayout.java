
package genj.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
  private Set<Component> components = new HashSet<Component>();
  
  
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
        return new Row(attrs);
      
      if ("col".equals(element))
        return new Column(attrs);
      
      if ("table".equals(element))
        return new Table(attrs);
      
      return new Cell(element, attrs);
    }
    
    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
      
      
      if (stack==null||stack.size()==0)
        throw new SAXException("unexpected /element");

      
      stack.pop();
      
      
      if (stack.isEmpty())
        throw DONE;
    }
    
  };

  
  private static abstract class Block implements Cloneable {
    
    Insets padding = this instanceof Cell ? new Insets(1,1,1,1) : new Insets(0,0,0,0);
    
    
    private Dimension preferred;

    
    Point weight;
    Point grow;
    int cols = 1;
    
    Block(Attributes attributes) {
      
      grow = new Point();
      
      
      if (attributes==null)
        return;
      
      
      String c = attributes.getValue("cols");
      if (c!=null) {
        cols = Integer.parseInt(c);
        if (cols<=0)
          throw new IllegalArgumentException("cols<=0");
      }      
      
      
      String gx = attributes.getValue("gx");
      if (gx!=null)
        grow.x = Integer.parseInt(gx)>0 ? 1 : 0;
      String gy = attributes.getValue("gy");
      if (gy!=null)
        grow.y = Integer.parseInt(gy)>0 ? 1 : 0;
      
      
      String pad = attributes.getValue("pad");
      if (pad!=null) {
        String[] pads = pad.split(",");
        switch (pads.length) {
        case 0: break;
        case 1: padding.set(Integer.parseInt(pads[0]), Integer.parseInt(pads[0]), Integer.parseInt(pads[0]), Integer.parseInt(pads[0])); break;
        case 2: padding.set(Integer.parseInt(pads[0]), Integer.parseInt(pads[1]), Integer.parseInt(pads[0]), Integer.parseInt(pads[1])); break;
        case 3: padding.set(Integer.parseInt(pads[0]), Integer.parseInt(pads[1]), Integer.parseInt(pads[2]), Integer.parseInt(pads[1])); break;
        case 4: padding.set(Integer.parseInt(pads[0]), Integer.parseInt(pads[1]), Integer.parseInt(pads[2]), Integer.parseInt(pads[3])); break;
        default: 
          throw new IllegalArgumentException("invalid padding "+pad+" given ("+attributes+")");
        }
      }
    }

    
    protected Block clone() {
      try {
        return (Block)super.clone();
      } catch (CloneNotSupportedException cnse) {
        throw new Error();
      }
    }
    
    
    abstract boolean removeContent(Component component);

    
    abstract Block add(Block block);
    
    
    void invalidate(boolean recurse) {
      
      preferred = null;
      weight = null;
    }
    
    
    abstract Point weight();
    
    
    Point grow() {
      return grow;
    }
    
    
    final Dimension preferred() {
      if (preferred==null) {
        preferred = preferredImpl();
        preferred.width += padding.left + padding.right;
        preferred.height += padding.top+ padding.bottom;
      }
      return preferred;
    }
    
    abstract Dimension preferredImpl();
      
    
    final void layout(Rectangle in) {
      Rectangle avail = new Rectangle(in);
      avail.x += padding.left;
      avail.width -= padding.left+padding.right;
      avail.y += padding.top;
      avail.height -= padding.top+padding.bottom;
      layoutImpl(avail);
    }
    
    abstract void layoutImpl(Rectangle in);
    
    
    abstract Collection<Cell> getCells(Collection<Cell> collect);
    
    
    abstract List<Block> setContent(Object key, Component component, List<Block> path);    
    
    @Override
    public String toString() {
      StringBuffer result = new StringBuffer();
      toString(result);
      return result.toString();
    }
    
    protected abstract void toString(StringBuffer result);
    
  } 
  
  
  private static class Row extends Folder {

    Row(Attributes attr) {
      super(attr);
    }
    
    
    Dimension preferredFolder() {
      Dimension result = new Dimension();
      for (int i=0;i<subs.size();i++) {
        Dimension sub = ((Block)subs.get(i)).preferred();
        result.width += sub.width;
        result.height = Math.max(result.height, sub.height);
      }
      return result;
    }
    
    
    @Override
    Point weightFolder() {
      Point result = new Point();
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        result.x += sub.weight().x;
        result.y = Math.max(result.y, sub.weight().y);
      }      
      return result;
    }
    
    
    @Override
    void layoutFolder(Rectangle in) {
      
      
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
  
  
  private static abstract class Folder extends Block {
    
    protected transient Expander expander = null;
    
    
    ArrayList<Block> subs = new ArrayList<Block>(16);
    
    
    protected Folder(Attributes attr) {
      super(attr);
    }
    
    
    Block add(Block block) {
      subs.add(block);
      invalidate(false);
      return block;
    }
    
    protected void toString(StringBuffer result) {
      result.append("<"+getClass().getSimpleName()+">");
      for (int i=0;i<subs.size();i++)
        subs.get(i).toString(result);
      result.append("</"+getClass().getSimpleName()+">");
    }
    
    
    protected Folder clone() {
      Folder clone = (Folder)super.clone();
      clone.subs = new ArrayList<Block>(subs.size());
      for (int i=0;i<subs.size();i++)
        clone.subs.add( (Block)subs.get(i).clone() );
      return clone;
    }
    
    
    boolean removeContent(Component component) {

      if (expander==component)
        expander=null;
      
      
      for (int i=0;i<subs.size();i++) {
        Block sub = (Block)subs.get(i);
        if (sub.removeContent(component)) {
          invalidate(false);
          return true;
        }
      }
      
      
      return false;
      
    }
    
    
    void invalidate(boolean recurse) {
      super.invalidate(recurse);
      
      if (recurse) for (int i=0;i<subs.size();i++) {
        ((Block)subs.get(i)).invalidate(true);
      }
    }
    
    
    Collection<Cell> getCells(Collection<Cell> collect) {
      for (int i=0;i<subs.size();i++) 
        ((Block)subs.get(i)).getCells(collect);
      return collect;
    }
    
    
    List<Block> setContent(Object key, Component component, List<Block> path) {
      
      int lastKeyMatch = -1;
      
      
      for (int i=0; i<subs.size(); i++) {
        
        Block sub = subs.get(i);

        if (sub instanceof Cell && key instanceof String && key.equals(((Cell)sub).element)) 
          lastKeyMatch = i;

        
        if (!sub.setContent(key, component, path).isEmpty()) {
          path.add(this);
          if (component instanceof Expander) {
            int indent = ((Expander)component).getIndent();
            if (indent<path.size() && path.get(indent)==this)
              expander = (Expander)component;
          }
          return path;
        }
        
      }

      
      if (lastKeyMatch>=0) {
        Block clone = subs.get(lastKeyMatch).clone();
        subs.add(lastKeyMatch+1, clone);
        clone.setContent(key, component, path);
        path.add(this);
        return path;
      }

      
      return path;
    }

    
    final Dimension preferredImpl() {
      if (expander!=null&&expander.isCollapsed)
        return expander.getPreferredSize();
      
      return preferredFolder();
    }
    
    abstract Dimension preferredFolder();
      
    final Point grow() {
      return expander!=null&&expander.isCollapsed ? new Point() : grow;
    }
    
    
    final Point weight() {
      
      if (expander!=null&&expander.isCollapsed)
        return new Point();
      
      
      if (weight!=null)
        return weight;
      
      
      weight = weightFolder();
      
      
      return weight;
    }
    
    abstract Point weightFolder();
    
    
    final void layoutImpl(Rectangle in) {

      
      if (expander!=null&&expander.isCollapsed) {
        for (Block sub : subs) 
          sub.layout(new Rectangle(0,0));
        Dimension d = expander.getPreferredSize();
        expander.setBounds(in.x,in.y,d.width,d.height);
        return;
      }

      layoutFolder(in);
    }
    
    abstract void layoutFolder(Rectangle in);
  }
  
  
  private static class Column extends Folder {
    
    Column(Attributes attr) {
      super(attr);
    }
    
    
    @Override
    Dimension preferredFolder() {
      Dimension result = new Dimension();
      for (int i=0;i<subs.size();i++) {
        Dimension sub = ((Block)subs.get(i)).preferred();
        result.width = Math.max(result.width, sub.width);
        result.height += sub.height;
      }
      return result;
    }
    
    
    @Override
    Point weightFolder() {
      Point result = new Point();
      for (int i=0;i<subs.size();i++) {
        Point sub = ((Block)subs.get(i)).weight();
        result.x = Math.max(result.x, sub.x);
        result.y += sub.y;
      }      
      return result;
    }
    
    
    @Override
    void layoutFolder(Rectangle in) {

      
      double weight = 0;
      int spare = in.height;
      int grow = 0;
      for (Block sub : subs) {
        spare -= sub.preferred().height;
        weight += sub.weight().getY();
        grow += sub.grow().y;
      }
      double weightFactor = weight>0 ? spare/weight : 0;
      int growFactor = weightFactor==0 && grow>0 ? spare/grow : 0;
      
      
      Rectangle avail = new Rectangle(in.x, in.y, 0, 0);
      for (int i=0;i<subs.size();i++) {
        
        Block sub = (Block)subs.get(i);
        avail.x = in.x;
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
    
    
    private Point cellWeight = new Point();
    
    
    private Point2D.Double cellAlign = new Point2D.Double(0,0.5);

    
    private Cell(String text) {
      super(null);
      this.element = "text";
      attrs.put("value", text);
    }
    
    
    private Cell(String element, Attributes attributes) {
      
      super(attributes);
      
      
      this.element = element;
      
      for (int i=0,j=attributes.getLength();i<j;i++) 
        attrs.put(attributes.getQName(i), attributes.getValue(i));
      
      
      String wx = getAttribute("wx");
      if (wx!=null) {
        cellWeight.x = Integer.parseInt(wx);
        if (attributes.getValue("gx")==null)
          grow.x = 1;
      }
      String wy = getAttribute("wy");
      if (wy!=null) {
        cellWeight.y = Integer.parseInt(wy);
        if (attributes.getValue("gy")==null)
          grow.y = 1;
      }
      
      
      String ax = getAttribute("ax");
      if (ax!=null)
        cellAlign.x = Float.parseFloat(ax);
      String ay = getAttribute("ay");
      if (ay!=null)
        cellAlign.y = Float.parseFloat(ay);

      
    }
    
    
    protected Block clone()  {
      Cell clone = (Cell)super.clone();
      clone.component = null;
      return clone;
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
    
    
    boolean removeContent(Component component) {
      if (this.component==component) {
        this.component = null;
        invalidate(false);
        return true;
      }
      return false;
    }
    
    
    Dimension preferredImpl() {
      
      if (component==null||!component.isVisible())
        return new Dimension();
      Dimension result = new Dimension(component.getPreferredSize());
      Dimension max = component.getMaximumSize();
      result.width = Math.min(max.width, result.width);
      result.height = Math.min(max.height, result.height);
      return result;
    }
    
    @Override
    protected void toString(StringBuffer result) {
      result.append("<cell ");
      result.append(attrs);
      result.append("/>");
    }
    
    
    @Override
    Point weight() {
      return component==null ? new Point() : cellWeight;
    }
    
    
    void layoutImpl(Rectangle in) {
      
      if (component==null)
        return;
      
      
      Rectangle avail = new Rectangle(in.x, in.y, in.width, in.height);
      
      
      Dimension max = component.getMaximumSize();
      if (avail.width>max.width) {
        int extraX = avail.width-max.width;
        avail.x += extraX * cellAlign.x;
        avail.width = max.width;
      }
      if (avail.height>max.height) {
        int extraY = avail.height-max.height;
        avail.y += extraY * cellAlign.y;
        avail.height = max.height;
      }

      
      component.setBounds(avail);
    }
    
    
    @Override
    List<Block> setContent(Object key, Component component, List<Block> path) {
      
      if (  (key instanceof Cell&&key!=this)
         || (key instanceof String&&(!element.equals(key)||this.component!=null))
         || (key==null&&this.component!=null))
        return path;
      
      if (this.component!=null)
        throw new IllegalArgumentException("can't set component twice");
      
      this.component = component;
      path.add(this);
      
      return path;
    }
    
    
    Collection<Cell> getCells(Collection<Cell> collect) {
      collect.add(this);
      return collect;
    }

    @Override
    Block add(Block block) {
      throw new IllegalArgumentException("cell.add() not supported");
    }

  } 
  
  
  public void addLayoutComponent(Component comp, Object key) {
    
    if (components.contains(comp))
      throw new IllegalArgumentException("already added");

    List<Block> path = root.setContent(key, comp, new ArrayList<Block>());
    if (!path.isEmpty()) {
      components.add(comp);
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
    root.removeContent(comp);
    components.remove(comp);
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
      
      
      for (Component c : target.getComponents()) 
        if (!components.contains(c))
          addLayoutComponent(c, null);
    }
  }

  
  public Dimension preferredLayoutSize(Container parent) {
    Dimension result = root.preferred();
    Insets insets = parent.getInsets();
    result.width += insets.left + insets.right;
    result.height += insets.top + insets.bottom;
    return result;
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
      clone.components = new HashSet<Component>();
      clone.invalidated = false;
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new Error(e);
    }
  }
  
  
  private static class Table extends Folder {

    private ArrayList<Integer> rowHeights;
    private ArrayList<Integer> colWidths;
    private ArrayList<Integer> rowWeights;
    private ArrayList<Integer> colWeights;
    
    Table(Attributes attrs) {
      super(attrs);
    }
    
    private void calcGrid() {
      
      if (rowHeights!=null)
        return;
      
      rowHeights = new ArrayList<Integer>();
      colWidths = new ArrayList<Integer>();
      rowWeights = new ArrayList<Integer>();
      colWeights = new ArrayList<Integer>();
      
      
      for (int r=0;r<subs.size();r++) {
        Block row = subs.get(r);
        
        List<Block> cells;
        
        if (row instanceof Row) 
          cells = ((Row)row).subs;
        else
          cells = Collections.singletonList(row);
        
        Block cell;
        for (int c=0;c<cells.size();c += cell.cols) {
          cell = cells.get(c);
          Dimension d = cell.preferred();
          if (cell.cols==1) 
            grow(colWidths, c, d.width);
          grow(rowHeights, r, d.height);
          Point w = cell.weight();
          if (cell.cols==1)
            grow(colWeights, c, w.x);
          grow(rowWeights, r, w.y);
        }
      }
      
      
      for (int r=0;r<subs.size();r++) {
        Block row = subs.get(r);
        List<Block> cells;
        if (row instanceof Row) 
          cells = ((Row)row).subs;
        else
          cells = Collections.singletonList(row);
          
        Block cell;
        for (int c=0;c<cells.size();c += cell.cols) {
          cell = cells.get(c);
          Dimension d = cell.preferred();
          if (cell.cols==1)
            continue;
          
          int spannedWeight = 0;
          int spannedWidth = 0;
          if (c+cell.cols>colWidths.size())
            throw new IllegalArgumentException("cols out of bounds for "+cell);
          for (int j=0;j<cell.cols;j++) {
            spannedWidth += colWidths.get(c+j);
            spannedWeight += colWeights.get(c+j);
          }
          
          
          if (spannedWidth<d.width) {
            int missing = d.width-spannedWidth;
            for (int j=0;j<cell.cols;j++) {
              int share = spannedWeight>0 ? missing*colWeights.get(c+j)/spannedWeight : missing/cell.cols;
              grow( colWidths, c+j, colWidths.get(c+j) + share );
            }
          }
          
        }
      }
      
      
    }
    
    @Override
    void layoutFolder(Rectangle in) {

      
      Dimension preferred = preferred();
      calcGrid();
      
      
      float xWeightMultiplier = 0;
      if (in.width>preferred.width) {
        int w = 0;
        for (int i=0;i<colWeights.size();i++)
          w += colWeights.get(i);
        xWeightMultiplier = (in.width-preferred.width)/(float)w;
      }
      float yWeightMultiplier = 0;
      if (in.height>preferred.height) {
        int h = 0;
        for (int i=0;i<rowWeights.size();i++)
          h += rowWeights.get(i);
        yWeightMultiplier = (in.height-preferred.height)/(float)h;
      }
      
      
      Rectangle avail = new Rectangle(in.x, in.y, in.width, in.height);
      for (int r=0;r<subs.size();r++) {
        
        Block row = subs.get(r);
        
        List<Block> cells;
        
        if (row instanceof Row) 
          cells = ((Row)row).subs;
        else
          cells = Collections.singletonList(row);
        
        int x = avail.x;
        Block sub;
        for (int c=0;c<cells.size();) {
          sub = cells.get(c);
          int w = 0;
          for (int i=0;i<sub.cols;i++,c++) 
            w += colWidths.get(c) + (int)(colWeights.get(c)*xWeightMultiplier);
          w = Math.min( avail.x+avail.width-x, w);
          sub.layout(new Rectangle(x, avail.y, w, rowHeights.get(r)));
          x += w;
        }
        
        
        avail.y += rowHeights.get(r);
      }

      
    }
    
    private void grow(ArrayList<Integer> values, int i, Integer value) {
      while (values.size()<i+1)
        values.add(0);
      values.set(i, Math.max(values.get(i), value));
    }
    
    @Override
    Dimension preferredFolder() {
      
      
      calcGrid();
      
      
      Dimension result = new Dimension(0,0);
      for (int c=0;c<colWidths.size();c++)
        result.width += colWidths.get(c);
      for (int r=0;r<rowHeights.size();r++)
        result.height += rowHeights.get(r);
      
      
      return result;
    }

    @Override
    Point weightFolder() {
      
      
      calcGrid();
      
      
      Point result = new Point(0,0);
      for (int c=0;c<colWeights.size();c++)
        result.x += colWeights.get(c);
      for (int r=0;r<rowWeights.size();r++)
        result.y += rowWeights.get(r);
      
      
      return result;
    }
    
    @Override
    void invalidate(boolean recurse) {
      super.invalidate(recurse);
      
      rowHeights = null;
      colWidths = null;
      rowWeights = null;
      colWeights = null;
    }
  }
  
  
  public static class Expander extends JLabel {
    
    private String expandedLabel, collapsedLabel;
    private final static Icon FOLDED = GraphicsHelper.getIcon(8, collapsed(8));
    private final static Icon UNFOLDED = GraphicsHelper.getIcon(8, open(8));
    private boolean isCollapsed = false;
    private int indent = 1;
    
    private static Shape collapsed(int size) {
      GeneralPath shape = new GeneralPath();
      shape.moveTo(size/4, 0);
      shape.lineTo(size/4, size+1);
      shape.lineTo(size*3/4, size/2);
      shape.closePath();
      return shape;
    }
    
    private static Shape open(int size) {
      GeneralPath shape = new GeneralPath();
      shape.moveTo(0, size/4);
      shape.lineTo(size, size/4);
      shape.lineTo(size/2, size*3/4);
      shape.closePath();
      return shape;
    }
    
    
    public Expander(String expandedLabel, String collapsedLabel) {
      this(expandedLabel, collapsedLabel, 1);
    }
    
    
    public Expander(String label) {
      this(label, 1);
    }
    
    
    public Expander(String label, int indent) {
      this(label, label, indent);
      
    }
    
    public Expander(String expandedLabel, String collapsedLabel, int indent) {
      super(expandedLabel);
      this.collapsedLabel = collapsedLabel;
      this.expandedLabel = expandedLabel;
      this.indent = Math.max(1, indent);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      addMouseListener(new Mouser());
    }
    
    public int getIndent() {
      return indent;
    }
    
    public void setCollapsed(boolean set) {
      isCollapsed = set;
      setText(isCollapsed ? collapsedLabel : expandedLabel);
    }
    
    public boolean isCollapsed() {
      return isCollapsed;
    }

    public Icon getIcon() {
      return isCollapsed ? FOLDED : UNFOLDED;
    }
    
    private class Mouser extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) {
        
        setCollapsed(!isCollapsed);
        
        firePropertyChange("folded", !isCollapsed, isCollapsed);

        Component parent = getParent();
        if (parent instanceof JComponent)
          ((JComponent)parent).revalidate();
        else {
          parent.invalidate();
          parent.validate();
        }
        
        
      }
    }
    
  }
  
} 