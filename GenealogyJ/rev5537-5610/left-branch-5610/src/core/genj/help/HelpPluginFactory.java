package genj.help;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.gedcom.Context;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.view.ActionProvider;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HelpPluginFactory implements PluginFactory, ActionProvider {
  
  private Workbench workbench;

  
  public Object createPlugin(Workbench workbench) {
    this.workbench = workbench;
    return this;
  }

  public List<Action2> createActions(Context context, Purpose purpose) {
    List<Action2> result = new ArrayList<Action2>();
    if (purpose == Purpose.MENU) {
      Action2.Group help = new ActionProvider.HelpActionGroup();
      help.add(new ActionHelp());
      result.add(help);
    }
    return result;
  }

  public int getPriority() {
    return 0;
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
