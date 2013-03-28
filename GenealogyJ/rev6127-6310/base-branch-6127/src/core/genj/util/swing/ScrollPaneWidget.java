
package genj.util.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;


public class ScrollPaneWidget extends JScrollPane {
  
  private Draggin draggin;

  
  public ScrollPaneWidget(JComponent view) {
    super(view);
  }
  
  @Override
  public void setViewportView(Component view) {
    Component old = super.getViewport().getView();
    if (old!=null) {
      old.removeMouseListener(draggin);
      old.removeMouseMotionListener(draggin);
    }
    super.setViewportView(view);
    if (view!=null) {
      if (draggin==null)
        draggin = new Draggin();
      view.addMouseListener(draggin);
      view.addMouseMotionListener(draggin);
    }
  }

  public class Draggin extends MouseAdapter implements MouseMotionListener {
    
    private Point start = new Point();
    
    @Override
    public void mousePressed(MouseEvent e) {
      start.setLocation(e.getPoint());
    }
    @Override
    public void mouseReleased(MouseEvent e) {
      ((JComponent)e.getSource()).setCursor(null);
    }
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e) {
      ((JComponent)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      Point v = viewport.getViewPosition();
      int dx = e.getPoint().x - start.x; 
      int dy = e.getPoint().y - start.y;
      v.x = Math.min(Math.max(0, v.x-dx), Math.max(0,viewport.getView().getWidth ()-viewport.getWidth ()));
      v.y = Math.min(Math.max(0, v.y-dy), Math.max(0,viewport.getView().getHeight()-viewport.getHeight())); 
      viewport.setViewPosition(v);
    }
  }

}
