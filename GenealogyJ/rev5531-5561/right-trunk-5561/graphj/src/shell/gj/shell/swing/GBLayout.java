
package gj.shell.swing;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


public class GBLayout {
  
  
  private GridBagLayout layout = new GridBagLayout();
  
  
  private JPanel panel;
  
  
  public GBLayout(JPanel panel) {
    this.panel = panel;
    panel.removeAll();
    panel.setLayout(layout);
  }
  
  
  public void add(JComponent component, int x, int y, int w, int h, boolean growx, boolean growy, boolean fillx, boolean filly) {

    
    panel.add(component);
    
    
    GridBagConstraints constraints = new GridBagConstraints(
      x,y,w,h,
      growx ? 1 : 0,
      growy ? 1 : 0,
      GridBagConstraints.WEST,
      fillx&&filly ? GridBagConstraints.BOTH : (fillx?GridBagConstraints.HORIZONTAL:GridBagConstraints.VERTICAL)
      ,new Insets(0,0,0,0),0,0);
      
    layout.setConstraints(component,constraints);
    
    
  }

}
