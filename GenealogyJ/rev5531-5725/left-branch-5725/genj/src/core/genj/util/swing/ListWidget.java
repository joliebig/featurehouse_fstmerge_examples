
package genj.util.swing;


import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class ListWidget extends JList {
  
  
  private static final ListCellRenderer RENDERER = new Renderer();

  
  public ListCellRenderer getCellRenderer() {
    return RENDERER;
  }
  
    
  private static class Renderer extends DefaultListCellRenderer {
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof Action2) {
        Action2 action = (Action2)value; 
        setText(action.getText());
        setIcon(action.getImage());
      }
      return this;
    }
  } 
  
} 

