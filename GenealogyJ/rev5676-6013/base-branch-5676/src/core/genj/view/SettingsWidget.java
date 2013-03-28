
package genj.view;

import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


 class SettingsWidget extends JPanel {
  
  
  private static Map cache = new WeakHashMap();
  
  
  private JPanel pSettings,pActions;
  private ActionApply apply = new ActionApply();
  private ActionReset reset = new ActionReset();
  
  
  private Settings settings;
  
  
  private ViewManager viewManager;
  
  
  protected SettingsWidget(ViewManager manager) {
    
    
    viewManager = manager;
    
    
    pSettings = new JPanel(new BorderLayout());

    
    JPanel pActions = new JPanel();

    ButtonHelper bh = new ButtonHelper().setContainer(pActions);
    
    bh.create(apply);
    bh.create(reset);
    bh.create(new ActionClose());

    
    setLayout(new BorderLayout());
    add(pSettings,"Center");
    add(pActions ,"South" );
    
    
  }

  
  protected void setView(ViewHandle handle) {
    
    
    pSettings.removeAll();
    
    
    settings = getSettings(handle.getView());
    if (settings!=null) {
      settings.setView(handle.getView());
      JComponent editor = settings.getEditor();
      editor.setBorder(new TitledBorder(handle.getTitle()));
      pSettings.add(editor, BorderLayout.CENTER);
      settings.reset();
    }
      
    
    apply.setEnabled(settings!=null);
    reset.setEnabled(settings!=null);
    
    
    pSettings.revalidate();
    pSettings.repaint();
    
    
  }

  
  private class ActionClose extends Action2 {
    private ActionClose() {
      setText(ViewManager.RESOURCES, "view.close");
    }
    protected void execute() {
      WindowManager.getInstance(getTarget()).close("settings");
    }
  } 
  
  
  private class ActionApply extends Action2 {
    protected ActionApply() { 
      setText(ViewManager.RESOURCES, "view.apply"); 
      setEnabled(false);
    }
    protected void execute() {
      settings.apply();
    }
  }

  
  private class ActionReset extends Action2 {
    protected ActionReset() { 
      setText(ViewManager.RESOURCES, "view.reset"); 
      setEnabled(false);
    }
    protected void execute() {
      settings.reset();
    }
  }
  
     static boolean hasSettings(JComponent view) {
    try {
      if (Settings.class.isAssignableFrom(Class.forName(view.getClass().getName()+"Settings")))
      return true;
    } catch (Throwable t) {
    }
    return false;
  }
  
  
   Settings getSettings(JComponent view) {
    
    
    Class viewType = view.getClass(); 
    Settings result = (Settings)cache.get(viewType);
    if (result!=null) return result;
    
    
    String type = viewType.getName()+"Settings";
    try {
      result = (Settings)Class.forName(type).newInstance();
      result.init(viewManager);
      cache.put(viewType, result);
    } catch (Throwable t) {
      result = null;
      ViewManager.LOG.log(Level.WARNING, "couldn't instantiate settings for "+view, t);
    }
    
    
    return result;
  }
  
} 

