

package edu.rice.cs.drjava.ui;

import javax.swing.text.*;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.plaf.*;


public class ReverseHighlighter extends DefaultHighlighter {
  
  
  public ReverseHighlighter() { drawsLayeredHighlights = true; }
  
  
  
  
  public void paint(Graphics g) {
    
    int len = _highlights.size();
    for (int i = 0; i < len; i++) {
      HighlightInfo info = _highlights.get(i);
      if (! (info instanceof LayeredHighlightInfo)) {
        
        Rectangle a = component.getBounds();
        Insets insets = component.getInsets();
        a.x = insets.left;
        a.y = insets.top;
        a.width -= insets.left + insets.right;
        a.height -= insets.top + insets.bottom;
        for (; i < len; i++) {
          info = _highlights.get(i);
          if (! (info instanceof LayeredHighlightInfo)) {
            Highlighter.HighlightPainter p = info.getPainter();
            p.paint(g, info.getStartOffset(), info.getEndOffset(),
                    a, component);
          }
        }
      }
    }
  }
  
  
  public void install(JTextComponent c) {
    component = c;
    removeAllHighlights();
  }
  
  
  public void deinstall(JTextComponent c) {
    component = null;
  }
  
  
  
  
  public Object addHighlight(int p0, int p1, Highlighter.HighlightPainter p) throws BadLocationException {
    Document doc = component.getDocument();
    HighlightInfo i = (getDrawsLayeredHighlights() &&
                       (p instanceof LayeredHighlighter.LayerPainter)) ?
      new LayeredHighlightInfo() : new HighlightInfo();
    i._painter = p;
    
    i.p0 = doc.createPosition(p0);
    i.p1 = doc.createPosition(p1);
    
    int insertPos = _highlights.size();

    if (p instanceof DrJavaHighlightPainter) {
      while (insertPos > 0) {
        HighlightInfo hli = _highlights.get( insertPos-1 );
        if (hli.getPainter() instanceof DrJavaHighlightPainter)
          --insertPos;
        else break;
      }
    } else if (p instanceof DefaultHighlightPainter) {
      while (insertPos > 0) {
        HighlightInfo hli = _highlights.get( insertPos-1 );
        if (hli.getPainter() instanceof DefaultHighlightPainter)
          --insertPos;
        else break;
      }
    } else if (p instanceof DefaultFrameHighlightPainter) {
      while (insertPos > 0) {
        HighlightInfo hli = _highlights.get( insertPos-1 );
        if (hli.getPainter() instanceof DefaultHighlightPainter || hli.getPainter() instanceof DefaultFrameHighlightPainter)
          --insertPos;
        else break;
      }
    } else {
      insertPos = 0;
    }
    _highlights.add(insertPos, i);
    
    safeDamageRange(p0, p1);
    return i;
  }
  
  
  public void removeHighlight(Object tag) {
    if (tag instanceof LayeredHighlightInfo) {
      LayeredHighlightInfo lhi = (LayeredHighlightInfo)tag;
      if (lhi.width > 0 && lhi.height > 0) {
        component.repaint(lhi.x, lhi.y, lhi.width, lhi.height);
      }
    }
    else {
      HighlightInfo info = (HighlightInfo) tag;
      safeDamageRange(info.p0, info.p1);
    }
    _highlights.remove(tag);
  }
  
  
  public void removeAllHighlights() {
    TextUI mapper = component.getUI();
    if (getDrawsLayeredHighlights()) {
      int len = _highlights.size();
      if (len != 0) {
        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;
        int p0 = -1;
        int p1 = -1;
        for (int i = 0; i < len; i++) {
          HighlightInfo hi = _highlights.get(i);
          if (hi instanceof LayeredHighlightInfo) {
            LayeredHighlightInfo info = (LayeredHighlightInfo)hi;
            minX = Math.min(minX, info.x);
            minY = Math.min(minY, info.y);
            maxX = Math.max(maxX, info.x + info.width);
            maxY = Math.max(maxY, info.y + info.height);
          }
          else {
            if (p0 == -1) {
              p0 = hi.p0.getOffset();
              p1 = hi.p1.getOffset();
            }
            else {
              p0 = Math.min(p0, hi.p0.getOffset());
              p1 = Math.max(p1, hi.p1.getOffset());
            }
          }
        }
        if (minX != maxX && minY != maxY) {
          component.repaint(minX, minY, maxX - minX, maxY - minY);
        }
        if (p0 != -1) {
          try {
            safeDamageRange(p0, p1);
          } catch (BadLocationException e) {}
        }
        _highlights.clear();
      }
    }
    else if (mapper != null) {
      int len = _highlights.size();
      if (len != 0) {
        int p0 = Integer.MAX_VALUE;
        int p1 = 0;
        for (int i = 0; i < len; i++) {
          HighlightInfo info = _highlights.get(i);
          p0 = Math.min(p0, info.p0.getOffset());
          p1 = Math.max(p1, info.p1.getOffset());
        }
        try {
          safeDamageRange(p0, p1);
        } catch (BadLocationException e) {}
        
        _highlights.clear();
      }
    }
  }
  
  
  public void changeHighlight(Object tag, int p0, int p1) throws BadLocationException {
    Document doc = component.getDocument();
    if (tag instanceof LayeredHighlightInfo) {
      LayeredHighlightInfo lhi = (LayeredHighlightInfo)tag;
      if (lhi.width > 0 && lhi.height > 0) {
        component.repaint(lhi.x, lhi.y, lhi.width, lhi.height);
      }
      
      
      lhi.width = lhi.height = 0;
      
      lhi.p0 = doc.createPosition(p0);
      lhi.p1 = doc.createPosition(p1);
      safeDamageRange(Math.min(p0, p1), Math.max(p0, p1));
    }
    else {
      HighlightInfo info = (HighlightInfo) tag;
      int oldP0 = info.p0.getOffset();
      int oldP1 = info.p1.getOffset();
      if (p0 == oldP0) safeDamageRange(Math.min(oldP1, p1), Math.max(oldP1, p1));
      else if (p1 == oldP1) safeDamageRange(Math.min(p0, oldP0), Math.max(p0, oldP0));
      else {
        safeDamageRange(oldP0, oldP1);
        safeDamageRange(p0, p1);
      }
      
      info.p0 = doc.createPosition(p0);
      info.p1 = doc.createPosition(p1);
      
      
    }
  }
  
  
  public Highlighter.Highlight[] getHighlights() {
    int size = _highlights.size();
    if (size == 0) {
      return noHighlights;
    }
    Highlighter.Highlight[] h = _highlights.toArray(EMTPY_HIGHLIGHTS);
    return h;
  }
  
  private static final Highlight[] EMTPY_HIGHLIGHTS = new Highlighter.Highlight[0];
  
  
  public void paintLayeredHighlights(Graphics g, int p0, int p1,
                                     Shape viewBounds,
                                     JTextComponent editor, View view) {
    for (int counter = _highlights.size() - 1; counter >= 0; counter--) {
      Object tag = _highlights.get(counter);
      if (tag instanceof LayeredHighlightInfo) {
        LayeredHighlightInfo lhi = (LayeredHighlightInfo)tag;
        int start = lhi.getStartOffset();
        int end = lhi.getEndOffset();
        if ((p0 < start && p1 > start) ||
            (p0 >= start && p0 < end)) {
          lhi.paintLayeredHighlights(g, p0, p1, viewBounds,
                                     editor, view);
        }
      }
    }
  }
  
  
  private void safeDamageRange(final Position p0, final Position p1) {
    safeDamager.damageRange(p0, p1);
  }
  
  
  private void safeDamageRange(int a0, int a1) throws BadLocationException {
    Document doc = component.getDocument();
    
    safeDamageRange(doc.createPosition(a0), doc.createPosition(a1));
  }
  
  
  public void setDrawsLayeredHighlights(boolean newValue) {
    drawsLayeredHighlights = newValue;
  }
  
  public boolean getDrawsLayeredHighlights() {
    return drawsLayeredHighlights;
  }
  
  
  
  private final static Highlighter.Highlight[] noHighlights =
    new Highlighter.Highlight[0];
  private ArrayList<HighlightInfo> _highlights = new ArrayList<HighlightInfo>();  
  private JTextComponent component;
  private boolean drawsLayeredHighlights;
  private SafeDamager safeDamager = new SafeDamager();
  
  
  public static class DefaultFrameHighlightPainter extends LayeredHighlighter.LayerPainter {
    
    
    public DefaultFrameHighlightPainter(Color c, int t) {
      color = c;
      thickness = t;
    }
    
    
    public Color getColor() { return color; }
    
    
    public int getThickness() { return thickness; }
    
    
    
    private void drawRectThick(Graphics g, int x, int y, int width, int height, int thick) {
      if (thick < 2) { g.drawRect(x, y, width, height); }
      else {
        g.fillRect(x, y,              width, thick);
        g.fillRect(x, y+height-thick, width, thick);
        g.fillRect(x, y,              thick, height);
        g.fillRect(x+width-thick, y,  thick, height);
      }
    }
    
    
    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
      Rectangle alloc = bounds.getBounds();
      try {
        
        TextUI mapper = c.getUI();
        Rectangle p0 = mapper.modelToView(c, offs0);
        Rectangle p1 = mapper.modelToView(c, offs1);
        
        
        Color color = getColor();
        
        if (color == null)  g.setColor(c.getSelectionColor());
        else  g.setColor(color);

        if (p0.y == p1.y) { 
          Rectangle r = p0.union(p1);
          drawRectThick(g, r.x, r.y, r.width, r.height, thickness);
        } 
        else { 
          int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
          drawRectThick(g, p0.x, p0.y, p0ToMarginWidth, p0.height, thickness);
          if ((p0.y + p0.height) != p1.y)
            drawRectThick(g, alloc.x, p0.y + p0.height, alloc.width, p1.y - (p0.y + p0.height), thickness);
          drawRectThick(g, alloc.x, p1.y, (p1.x - alloc.x), p1.height, thickness);
        }
      } 
      catch (BadLocationException e) {  }
    }
    
    
    
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
      Color color = getColor();
      
      if (color == null) g.setColor(c.getSelectionColor());
      else g.setColor(color);

      if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) { 
        Rectangle alloc;
        if (bounds instanceof Rectangle) alloc = (Rectangle)bounds;
        else alloc = bounds.getBounds();

        drawRectThick(g, alloc.x, alloc.y, alloc.width, alloc.height, thickness);
        return alloc;
      }
      else { 
        try {
          
          Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);
          Rectangle r = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
          
          drawRectThick(g, r.x, r.y, r.width, r.height, thickness);
          return r;
        } 
        catch (BadLocationException e) {  }
      }
      
      return null;
    }
    
    private Color color;
    private int thickness;
  }
  
  
  
  public static class DefaultUnderlineHighlightPainter extends LayeredHighlighter.LayerPainter {
    
    
    public DefaultUnderlineHighlightPainter(Color c, int t) {
      color = c;
      thickness = t;
    }
    
    
    public Color getColor() { return color; }
    
    
    public int getThickness() { return thickness; }
    
    
    
    private void drawUnderline(Graphics g, int x, int y, int width, int height, int thick) {
      g.fillRect(x, y+height-thick, width, thick);
    }
    
    
    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
      Rectangle alloc = bounds.getBounds();
      try {
        
        TextUI mapper = c.getUI();
        Rectangle p0 = mapper.modelToView(c, offs0);
        Rectangle p1 = mapper.modelToView(c, offs1);
        
        
        Color color = getColor();
        
        if (color == null) g.setColor(c.getSelectionColor());
        else g.setColor(color);

        if (p0.y == p1.y) { 
          Rectangle r = p0.union(p1);
          drawUnderline(g, r.x, r.y, r.width, r.height, thickness);
        } 
        else { 
          int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
          drawUnderline(g, p0.x, p0.y, p0ToMarginWidth, p0.height, thickness);
          if ((p0.y + p0.height) != p1.y)
            drawUnderline(g, alloc.x, p0.y + p0.height, alloc.width, p1.y - (p0.y + p0.height), thickness);

          drawUnderline(g, alloc.x, p1.y, (p1.x - alloc.x), p1.height, thickness);
        }
      } 
      catch (BadLocationException e) {  }
    }
    
    
    
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
      Color color = getColor();
      
      if (color == null) g.setColor(c.getSelectionColor());
      else g.setColor(color);

      if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) { 
        Rectangle alloc;
        if (bounds instanceof Rectangle) alloc = (Rectangle)bounds;
        else alloc = bounds.getBounds();

        drawUnderline(g, alloc.x, alloc.y, alloc.width, alloc.height, thickness);
        return alloc;
      }
      else { 
        try {
          
          Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);
          Rectangle r = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
          drawUnderline(g, r.x, r.y, r.width, r.height, thickness);
          return r;
        } catch (BadLocationException e) {  }
      }
      
      return null;
    }
    
    private Color color;
    private int thickness;
  }
  
  class HighlightInfo implements Highlighter.Highlight {
    
    Position p0;
    Position p1;
    Highlighter.HighlightPainter _painter;
    
    public int getStartOffset() { return p0.getOffset(); }
    
    public int getEndOffset() { return p1.getOffset(); }
    
    public Highlighter.HighlightPainter getPainter() { return _painter; }
    

  }
  
  
  
  public static class DrJavaHighlightPainter extends DefaultHighlightPainter {
    
    public DrJavaHighlightPainter(Color c) { super(c); }
  }
  
  
  
  class LayeredHighlightInfo extends HighlightInfo {
    
    void union(Shape bounds) {
      if (bounds == null)
        return;
      
      Rectangle alloc;
      if (bounds instanceof Rectangle) alloc = (Rectangle)bounds;
      else alloc = bounds.getBounds();

      if (width == 0 || height == 0) {
        x = alloc.x;
        y = alloc.y;
        width = alloc.width;
        height = alloc.height;
      }
      else {
        width = Math.max(x + width, alloc.x + alloc.width);
        height = Math.max(y + height, alloc.y + alloc.height);
        x = Math.min(x, alloc.x);
        width -= x;
        y = Math.min(y, alloc.y);
        height -= y;
      }
    }
    
    
    void paintLayeredHighlights(Graphics g, int p0, int p1, Shape viewBounds, JTextComponent editor, View view) {
      int start = getStartOffset();
      int end = getEndOffset();
      
      p0 = Math.max(start, p0);
      p1 = Math.min(end, p1);
      
      LayeredHighlighter.LayerPainter lp = (LayeredHighlighter.LayerPainter) _painter;
      union(lp.paintLayer(g, p0, p1, viewBounds, editor, view));
    }
    
    int x;
    int y;
    int width;
    int height;
  }
  
  
  
  class SafeDamager implements Runnable {
    private ArrayList<Position> p0 = new ArrayList<Position>(10);
    private ArrayList<Position> p1 = new ArrayList<Position>(10);
    private Document lastDoc = null;
    
    
    public synchronized void run() {
      if (component != null) {
        TextUI mapper = component.getUI();
        if (mapper != null && lastDoc == component.getDocument()) { 
          int len = p0.size();
          for (int i = 0; i < len; i++){
            mapper.damageRange(component, p0.get(i).getOffset(), p1.get(i).getOffset());
          }
        }
      }
      p0.clear();
      p1.clear();
      
      
      lastDoc = null;
    }
    
    
    public synchronized void damageRange(Position pos0, Position pos1) {
      if (component == null) {
        p0.clear();
        lastDoc = null;
        return;
      }
      
      boolean addToQueue = p0.isEmpty();
      Document curDoc = component.getDocument();
      if (curDoc != lastDoc) {
        if (!p0.isEmpty()) {
          p0.clear();
          p1.clear();
        }
        lastDoc = curDoc;
      }
      p0.add(pos0);
      p1.add(pos1);
      
      if (addToQueue) EventQueue.invokeLater(this);  
    }
  }
}
