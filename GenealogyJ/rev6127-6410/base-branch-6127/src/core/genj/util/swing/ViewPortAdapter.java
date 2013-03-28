
package genj.util.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;


public class ViewPortAdapter extends JComponent {
  
  
  private JComponent comp;

  
  public ViewPortAdapter(JComponent c) {
    comp = c;
    setLayout(new GridBagLayout());
    add(comp, new GridBagConstraints());
  }
  
  @Override
  public synchronized void addMouseListener(MouseListener l) {
    comp.addMouseListener(l);
  }
  
  @Override
  public synchronized void removeMouseListener(MouseListener l) {
    comp.removeMouseListener(l);
  }
  
  @Override
  public synchronized void addMouseMotionListener(MouseMotionListener l) {
    comp.addMouseMotionListener(l);
  }
  
  @Override
  public synchronized void removeMouseMotionListener(MouseMotionListener l) {
    comp.removeMouseMotionListener(l);
  }
  
  
  public Dimension getPreferredSize() {
    return comp.getPreferredSize();
  }
  
  
  public JComponent getComponent() {
    return comp;
  }
  } 
