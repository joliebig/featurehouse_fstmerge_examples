
package genj.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ViewPortOverview extends Panel {
  
  
  private final static int DIM_RESIZE = 6;

  
  private JViewport viewport;
  
  
  private Rectangle last = null; 

  
  public ViewPortOverview(JViewport viewpOrt) {
    viewport = viewpOrt;
    EventGlue glue = new EventGlue();    
    viewport.addChangeListener(glue);
    addMouseListener(glue); 
    addMouseMotionListener(glue);
    viewport.addComponentListener(glue);
  }
  
  
  public void paint(Graphics g) {
    
    
    Dimension dim = getSize();
    g.setColor(Color.white);
    g.fillRect(0,0,dim.width,dim.height);

    
    Point2D zoom = getZoom();

    
    renderContent(g, zoom.getX(), zoom.getY());

    
    g.setColor(Color.blue);
    g.drawRect(0,0,dim.width-1,dim.height-1);

    
    last = null;
    
    
    renderIndicator(g, zoom);
    
    
  }
  
  
  protected void renderContent(Graphics g, double zoomx, double zoomy) {
  }
  
  
  private void renderIndicator(Graphics g, Point2D zoom) {
    
    if (last!=null) {
      g.setColor(Color.white);
      g.setXORMode(Color.lightGray);
      g.fillRect(last.x, last.y, last.width, last.height);
    }
    
    if (zoom==null) zoom = getZoom();
    
    Rectangle shown = viewport.getViewRect();
    last = new Rectangle(
      (int)(shown.x     * zoom.getX()),
      (int)(shown.y     * zoom.getY()),
      (int)(shown.width * zoom.getX()),
      (int)(shown.height* zoom.getY())
    );
    
    g.setColor(Color.lightGray);
    g.setXORMode(Color.white);
    g.fillRect(last.x, last.y, last.width, last.height);
    
  }
  
  
  private Point2D getZoom() {

    
    Dimension avail = getSize();
    
    Component c = viewport.getView();
    if (c instanceof ViewPortAdapter) c = ((ViewPortAdapter)c).getComponent();
    Dimension view = c.getSize();
    
    
    return new Point2D.Double(
      ((double)avail.width )/view.width ,
      ((double)avail.height)/view.height
    );
  }
  
  
  private class EventGlue
    extends ComponentAdapter
  	implements ChangeListener, MouseListener, MouseMotionListener
  {
    
    private Point dragOffset = null;  
    
    private boolean isResize = false;
    
    public void stateChanged(ChangeEvent e) {
      if (!isVisible()) return;
      renderIndicator(getGraphics(), null);
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
      Point p = e.getPoint();
      
      isResize = isResize(p);
      
      dragOffset = last.contains(p) ? new Point(last.x-p.x, last.y-p.y) : null;
      
    }
    
    public void mouseReleased(MouseEvent e) {
      
      dragOffset = null;
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mouseDragged(MouseEvent e) {
      Point p = e.getPoint();
      
      if (isResize) {
        setSize(new Dimension(p.x, p.y));
        return;
      } 
      
      if (dragOffset==null) return;
      
      Point2D zoom = getZoom();
      Rectangle shown = viewport.getViewRect();
      int 
        x = (int)(((double)(p.x + dragOffset.x))/zoom.getX()),
        y = (int)(((double)(p.y + dragOffset.y))/zoom.getY());
      
      viewport.scrollRectToVisible(new Rectangle(
        x-shown.x,y-shown.y,shown.width,shown.height
      ));
      
    }

    
    public void mouseMoved(MouseEvent e) {
      
      Point p = e.getPoint();
      
      int cursor = Cursor.DEFAULT_CURSOR;
      
      if (last!=null&&last.contains(p))
        cursor = Cursor.MOVE_CURSOR;
      
      if (isResize(p))
        cursor = Cursor.SE_RESIZE_CURSOR;
      
      setCursor(Cursor.getPredefinedCursor(cursor));
    }
    
    
    private boolean isResize(Point p) {
      Dimension dim = getSize();
      return p.x>dim.width-DIM_RESIZE&&p.y>dim.height-DIM_RESIZE;
    }
    
    
    public void componentResized(ComponentEvent e) {
    	repaint();
    }

  } 

} 
