
package genj.view;

import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;


public abstract class SettingsAction<E extends JComponent> extends Action2 {
  
  private final static Resources RESOURCES = Resources.get(SettingsAction.class);
  
  public SettingsAction() {
    setImage(Images.imgSettings);
    setTip(RESOURCES.getString("view.settings.tip"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    E editor = getEditor();
    
    if (0!=DialogHelper.openDialog(
        RESOURCES.getString("view.edit.title"), 
        DialogHelper.QUESTION_MESSAGE, 
        editor, 
        Action2.okCancel(), 
        DialogHelper.getComponent(e)))
      return;

    commit(editor);
    
  }
  
  protected abstract E getEditor();
  
  protected abstract void commit(E editor);
  
}
