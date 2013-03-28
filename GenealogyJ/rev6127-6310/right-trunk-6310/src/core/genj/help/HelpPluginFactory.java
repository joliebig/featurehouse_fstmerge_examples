package genj.help;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
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
      help.add(new Page(HelpWidget.WELCOME, Gedcom.getImage()));
      help.add(new Page(HelpWidget.MANUAL, HelpView.IMG));
      result.add(help);
    }
  }

  
  private class Page extends Action2 {
    
    private String page;
    
    
    protected Page(String page, ImageIcon img) {
      setText(Resources.get(this).getString("help."+page));
      setImage(img);
      this.page = page;
    }

    
    public void actionPerformed(ActionEvent event) {
      HelpView view = (HelpView)workbench.openView(HelpViewFactory.class, new Context());
      view.setPage(page);
    }
  } 

}
