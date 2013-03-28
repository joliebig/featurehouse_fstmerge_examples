
package genj.util.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;


public class ViewPortAdapter extends JComponent {
  
  
  private JComponent comp;

  
  public ViewPortAdapter(JComponent c) {
    comp = c;
    setLayout(new GridBagLayout());
    add(comp, new GridBagConstraints());
  }
  
  
  public Dimension getPreferredSize() {
    return comp.getPreferredSize();
  }
  
  
  public JComponent getComponent() {
    return comp;
  }
  } 
