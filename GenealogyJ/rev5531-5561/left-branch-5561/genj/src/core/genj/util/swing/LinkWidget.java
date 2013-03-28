
package genj.util.swing;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class LinkWidget extends JLabel {
  
  
  private boolean hover = false;

  
  private Action2 action;
  
  
  private Color normal;
  
  
  public LinkWidget(Action2 action) {
    this(action.getText(), action.getImage());
    setToolTipText(action.getTip());
    this.action = action;
  }
  
  
  public LinkWidget(String text, Icon img) {
    super(text, img, SwingConstants.LEFT);
    addMouseListener(new Callback());
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }
  
  
  public LinkWidget(ImageIcon img) {
    this(null, img);
  }
  
  
  public LinkWidget() {
    this(null,null);
  }
   
  
  protected void fireActionPerformed() {
    if (action!=null)
      action.actionPerformed(new ActionEvent(this, 0, ""));
  }

  
  protected void paintComponent(Graphics g) {
    
    super.paintComponent(g);
    
    if (!hover) return;
    g.setColor(getForeground());
    g.drawLine(1,getHeight()-1,getWidth()-1-1,getHeight()-1);
    
  }
  
  
  private class Callback extends MouseAdapter {
    
    
    public void mouseClicked(MouseEvent e) {
      fireActionPerformed();
    }
    
    public void mouseExited(MouseEvent e) {
      hover = false;
      repaint();
    }
    
    public void mouseEntered(MouseEvent e) {
      hover = true;
      repaint();
    }
    
  } 

} 

