
package genj.util.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


public class HeadlessLabel extends JComponent {
    
  
  private int iconTextGap = 4;
  private int padding = 0;
  private String txt = "";
  private Icon icon;
  private float iconLocation = 0.0F;
  private boolean isOpaque = false;
  private Font font;
  private int horizontalAlignment = SwingConstants.LEFT;

  
  public HeadlessLabel() {
  }

  
  public HeadlessLabel(Font font) {
    setFont(font);
  }
  
  public void setHorizontalAlignment(int swingConstantAlignment) {
    switch (swingConstantAlignment) {
      case SwingConstants.LEFT:
        horizontalAlignment = SwingConstants.LEFT;
        break;
      case SwingConstants.RIGHT:
        horizontalAlignment = SwingConstants.RIGHT;
        icon = null;
      break;
    }
    
  }
  
  public void setFont(Font set) {
    font = set;
  }
  
  public Font getFont() {
    if (font==null)
      font = super.getFont();
    return font;
  }

  
  public void setText(String set) {
    txt = set!=null ? set : "";
  }
  
  @Override
  public void setBorder(Border border) {
    throw new IllegalArgumentException("don't set border on headless label");
  }
  
  public void setPadding(int padding) {
    this.padding = padding;
  }
  
  
  public void setIcon(Icon icOn) {
    icon = icOn;
    horizontalAlignment = SwingConstants.LEFT;
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
    FontMetrics fm = getFontMetrics(getFont());
    width = fm.stringWidth(txt);
    height = fm.getHeight();
    
    if (icon!=null) {
      width += icon.getIconWidth();
      height = Math.max(height,icon.getIconHeight());
    }
    
    if (txt.length()>0&&icon!=null) 
      width += iconTextGap;
    
    return new Dimension(width, height);
  }
    
  
  public void paint(Graphics g) {
    Font font = getFont();
    Dimension size = getSize();
    int x = 0, y = 0;
    
    if (isOpaque) {
      g.setColor(getBackground());
      g.fillRect(x,y,size.width,size.height);
    }
    
    if (padding>0) {
      x+=padding;
      y+=padding;
      size.width-=padding+padding;
      size.height-=padding+padding;
    }
    
    if (icon!=null) {
      int
        w = icon.getIconWidth(),
        h = icon.getIconHeight();
      icon.paintIcon(null, g, 0, (int)(iconLocation*(size.height - h)));
      x += w+iconTextGap;
      size.width -= w+iconTextGap;
    }
    
    if (horizontalAlignment==SwingConstants.RIGHT) 
      x += size.width - getFontMetrics(font).stringWidth(txt);
    
    g.setColor(getForeground());
    g.setFont(font);
    g.drawString(txt, x, getFontMetrics(font).getMaxAscent());       
    
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
