
package gj.shell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.Action;
import javax.swing.JMenuItem;


public class ShapeMenuItemWidget extends JMenuItem {
  
  
  private Shape shape;
  
  
  public ShapeMenuItemWidget(Shape shape, Action action) {
    super(action);
    this.shape = shape;
  }

  
  @Override
  public Dimension getPreferredSize() {
    Dimension size = shape.getBounds().getSize();
    return new Dimension(size.width+5, size.height+5);
  }

  
  @Override
  public void paintComponent(Graphics g) {
    
    super.paintComponent(g);

    Dimension size = getSize();
    Graphics2D graphics = (Graphics2D)g;
    
    graphics.setColor(Color.black);
    graphics.translate(size.width/2, size.height/2);
    graphics.draw(shape);
    graphics.translate(-size.width/2, -size.height/2);
  }

}
