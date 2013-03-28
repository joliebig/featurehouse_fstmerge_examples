
package genj.util.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;


public class ToolbarWidget extends JToolBar {
  
  @Override
  public JButton add(Action a) {
    return patch(super.add(a));
  }
  
  public static JButton patch(JButton button) {
    
    button.setRequestFocusEnabled(false);
    button.setFocusable(false);

    
    Icon i = button.getIcon();
    if (i instanceof ImageIcon)
      button.setDisabledIcon( ((ImageIcon)i).getGrayedOut() );
    
    return button;
  }
  

}
