
package gj.shell.swing;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;


public class SwingHelper {
  
  public static final int
    DLG_YES_NO = JOptionPane.YES_NO_OPTION,
    DLG_OK_CANCEL = JOptionPane.OK_CANCEL_OPTION,
    DLG_OK = -1;
    
  public static final int 
    OPTION_NO     = JOptionPane.NO_OPTION,
    OPTION_YES    = JOptionPane.YES_OPTION,
    OPTION_OK     = JOptionPane.OK_OPTION,
    OPTION_CANCEL = JOptionPane.CANCEL_OPTION;

  
  public static JSplitPane getSplitPane(boolean vertical, JComponent left, JComponent right) {
    JSplitPane result = new JSplitPane(
      vertical ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT,
      left, right
    );
    result.setDividerSize(3);
    result.setDividerLocation(0.5D);
    return result;
  }
  
  
  public static String showDialog(JComponent parent, String title, String message) {
    return JOptionPane.showInputDialog(parent,message,title,JOptionPane.QUESTION_MESSAGE );
  }
  
  
  public static int showDialog(JComponent parent, String title, Object content, int type) {
    return JOptionPane.showConfirmDialog(parent,content,title,type);
  }
  
  
  public static JCheckBoxMenuItem getCheckBoxMenuItem(final Action2 action) {
    
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(action);
    result.setModel(new DefaultButtonModel() {
      @Override
      public boolean isSelected() {
        return action.isSelected();
      }
    });
    
    return result;
  }
  
}
