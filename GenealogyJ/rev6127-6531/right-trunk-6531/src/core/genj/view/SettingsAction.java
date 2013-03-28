
package genj.view;

import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;


public abstract class SettingsAction extends Action2 {
  
  private final static Resources RESOURCES = Resources.get(SettingsAction.class);
  
  public SettingsAction() {
    setImage(Images.imgSettings);
    setTip(RESOURCES.getString("view.settings.tip"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    JComponent editor = getEditor();
    
    DialogHelper.openDialog(
        RESOURCES.getString("view.settings.tip"), 
        DialogHelper.QUESTION_MESSAGE, 
        editor, 
        Action2.okOnly(), 
        e);

  }
  
  protected abstract JComponent getEditor();
  
}
