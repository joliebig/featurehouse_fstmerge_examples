
package genj.util.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;


public class HeadlessLabel extends JComponent {
    
  
  private int ICON_TEXT_GAP = 4;

  
  private String txt = "";
        
  
  private View view;
    
  
  private Icon icon;
  
  
  private float iconLocation = 0.0F;
    
  
  private boolean isOpaque = false;
  
  
  private Font font;

  
  public HeadlessLabel() {
  }

  
  public HeadlessLabel(Font font) {
    setFont(font);
  }
  
  public void setFont(Font set) {
    font = set;
  }
  
  public Font getFont() {
    if (font==null)
      font = super.getFont();
    return font;
  }

  
  public View setHTML(String set) {
    return setView(BasicHTML.createHTMLView(this, set));
  }
  
  
  public View setView(View set) {
    view = set;
    txt = "";
    return set;
  }
    
  
  public void setText(String set) {
    view = null;
    txt = set!=null ? set : "";
  }
    
  
  public void setIcon(Icon icOn) {
    icon = icOn;
  }
    
  
  public boolean isOpaque() { 
    return isOpaque;
  }
    
  
  public void setOpaque(boolean set) {
    isOpaque = set;
  }
  
  
  public void setIconLocation(float set) {
    iconLocation = set;
  }

  
  public Dimension getPreferredSize() {
    int 
      width, 
      height;
    
    if (view!=null) {
      width = (int)view.getPreferredSpan(View.X_AXIS);
      height= (int)view.getPreferredSpan(View.Y_AXIS);
    } else {
      FontMetrics fm = getFontMetrics(getFont());
      width = fm.stringWidth(txt);
      height = fm.getHeight();
    }
    
    if (icon!=null) {
      width += icon.getIconWidth();
      height = Math.max(height,icon.getIconHeight());
    }
    
    if ((view!=null||txt.length()>0)&&icon!=null) 
      width += ICON_TEXT_GAP;
    



    
    return new Dimension(width, height);
  }
    
  
  public void paint(Graphics g) {
    Rectangle bounds = getBounds();
    bounds.x=0;
    bounds.y=0;
    
    if (isOpaque) {
      g.setColor(getBackground());
      g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
    }
    
    if (icon!=null) {
      int
        w = icon.getIconWidth(),
        h = icon.getIconHeight();
      icon.paintIcon(null, g, 0, (int)(iconLocation*(bounds.height - h)));
      bounds.x += w+ICON_TEXT_GAP;
      bounds.width -= w+ICON_TEXT_GAP;
    }
    
    g.setColor(getForeground());
    if (view!=null) {
      view.setSize(bounds.width, bounds.height);
      view.paint(g, bounds);
    } else {
      Font font = getFont();
      g.setFont(font);
      g.drawString(txt, bounds.x, getFontMetrics(font).getMaxAscent());       
    }
    
  }
  
  
  public float getAlignmentY() {
    return 0;
  }


  public void validate() {
  }

  public void revalidate() {
  }

  public void repaint(long tm, int x, int y, int width, int height) {
  }

  public void repaint(Rectangle r) { 
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
  }

  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
  }
    
} 
