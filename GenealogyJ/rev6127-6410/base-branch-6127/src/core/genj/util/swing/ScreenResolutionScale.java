
package genj.util.swing;

import genj.renderer.DPI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import javax.swing.JComponent;

public class ScreenResolutionScale extends JComponent {
  
  
  private DPI dpi = new DPI( 
    Toolkit.getDefaultToolkit().getScreenResolution(),
    Toolkit.getDefaultToolkit().getScreenResolution()
  );
  
  
  private final static float DPI2CM = 1F/2.54F;

    public ScreenResolutionScale(DPI dpi) {
    setDPI(dpi);
    addMouseMotionListener(new MouseGlue());
  }
  
  
  public DPI getDPI() {
    return dpi;
  }

  
  public void setDPI(DPI set) {
    dpi = set;
  }

  
  protected void paintComponent(Graphics graphcs) {
    
    
    graphcs.setColor(Color.white);
    graphcs.fillRect(0,0,getWidth(),getHeight());
    graphcs.setColor(Color.black);
    graphcs.drawRect(0,0,getWidth()-1,getHeight()-1);

    
    paintLabel(graphcs);
    
    
    paintScale(graphcs);
    
    
  }
  
    private void paintScale(Graphics graphcs) {

    
    UnitGraphics gw = new UnitGraphics(graphcs, DPI2CM * dpi.horizontal(), DPI2CM * dpi.vertical());
    gw.setAntialiasing(true);

    
    gw.setFont(new Font("Arial", Font.PLAIN, 10));
    
    
    Rectangle2D clip = gw.getClip();

    int X=1;
    do {
      
      gw.setColor(Color.gray);
      for (double x=0.1; x<0.9; x+=0.1)
        gw.draw(X-x,0,X-x,0.1);
      gw.setColor(Color.black);
      gw.draw(X,0,X,0.4);
      gw.draw(""+X, X, 1, 0.0D, 0.0D);
      
    } while (X++<clip.getMaxX());

    int Y=1;
    do {
      
      gw.setColor(Color.gray);
      for (double y=0.1; y<0.9; y+=0.1)
        gw.draw(0,Y-y,0.1,Y-y);
      gw.setColor(Color.black);
      gw.draw(0,Y,0.4,Y);
      gw.draw(""+Y, 1, Y, 0.0, 0.0);
      
    } while (Y++<clip.getMaxY());
    
  }
  
  
  private void paintLabel(Graphics graphcs) {
    graphcs.setColor(Color.black);
    FontMetrics fm = graphcs.getFontMetrics(); 
    int fh = fm.getHeight();
    
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    String[] txt = new String[]{
      ""+nf.format(dpi.horizontal()),
      "by",
      ""+nf.format(dpi.vertical()),
      "DPI"
    };
    for (int i = 0; i < txt.length; i++) {
      graphcs.drawString(
        txt[i], 
        getWidth()/2 - fm.stringWidth(txt[i])/2, 
        getHeight()/2 - txt.length*fh/2 + i*fh + fh
      );
    }
    
    graphcs.drawString("cm", 16, 16+fm.getAscent());
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(3*dpi.horizontal(), 3*dpi.vertical());
  }
  
  
  public Dimension getMinimumSize() {
    return new Dimension(64,64);
  }


    private class MouseGlue extends MouseAdapter implements MouseMotionListener {
    
    
    private boolean axis;
    
    
    private Point startPos = new Point();
    
    
    private Point startDPI = new Point();
    
    
    public void mouseMoved(MouseEvent e) {
      
      
      startPos.x = e.getPoint().x;
      startPos.y = e.getPoint().y;
      startDPI.x = dpi.horizontal();
      startDPI.y = dpi.vertical();

      
      axis = startPos.x>startPos.y;
      
      
      setCursor(Cursor.getPredefinedCursor(axis?Cursor.E_RESIZE_CURSOR:Cursor.S_RESIZE_CURSOR));
      
      
    }

    
    public void mouseDragged(MouseEvent e) {
      
      float 
        x = e.getPoint().x,
        y = e.getPoint().y;
      if (axis) 
        dpi = new DPI( (int)Math.max(10, startDPI.x * (x/startPos.x)), dpi.vertical());
      else     
        dpi = new DPI( dpi.horizontal(), (int)Math.max(10, startDPI.y * (y/startPos.y)));
        
      
      repaint();
    }
    
  } 
} 
