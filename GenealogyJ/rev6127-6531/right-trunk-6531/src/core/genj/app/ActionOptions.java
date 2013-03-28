
package genj.app;

import genj.option.OptionProvider;
import genj.option.OptionsWidget;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;

import java.awt.event.ActionEvent;


 class ActionOptions extends Action2 {
  
  private final static Resources RES = Resources.get(Workbench.class);
  
  
  protected ActionOptions() {
    setText(RES.getString("cc.menu.options"));
    setImage(OptionsWidget.IMAGE);
  }

  
  public void actionPerformed(ActionEvent event) {
    
    OptionsWidget widget = new OptionsWidget(getText());
    widget.setOptions(OptionProvider.getAllOptions());
    
    DialogHelper.openDialog(getText(), DialogHelper.INFORMATION_MESSAGE, widget, Action2.okOnly(), event);
    
  }
}