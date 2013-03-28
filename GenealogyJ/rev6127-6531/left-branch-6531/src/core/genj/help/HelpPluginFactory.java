package genj.help;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.gedcom.Context;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;

import java.awt.event.ActionEvent;

public class HelpPluginFactory implements PluginFactory, ActionProvider {
  
  private Workbench workbench;

  
  public Object createPlugin(Workbench workbench) {
    this.workbench = workbench;
    return this;
  }

  public void createActions(Context context, Purpose purpose, Group result) {
    if (purpose == Purpose.MENU) {
      Action2.Group help = new ActionProvider.HelpActionGroup();
      help.add(new ActionHelp());
      result.add(help);
    }
  }

  
  private class ActionHelp extends Action2 {
    
    protected ActionHelp() {
      setText(Resources.get(this).getString("help.content"));
      setImage(HelpView.IMG);
    }

    
    public void actionPerformed(ActionEvent event) {
      workbench.openView(HelpViewFactory.class, new Context());
    }
  } 

}
