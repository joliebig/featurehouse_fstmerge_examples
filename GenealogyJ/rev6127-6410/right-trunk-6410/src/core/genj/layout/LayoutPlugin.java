
package genj.layout;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.app.WorkbenchAdapter;
import genj.gedcom.Context;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LayoutPlugin extends WorkbenchAdapter implements PluginFactory, ActionProvider {
  
  private final static String
    LAYOUT_RESEARCH = "research",
    LAYOUT_PRESENTATION = "presentation";

  private final static ImageIcon IMG = new ImageIcon(Workbench.class,"images/Layout.png");
  private final static Logger LOG = Logger.getLogger("genj.layout");
  private final static Resources RES = Resources.get(LayoutPlugin.class);
  private final static Registry REGISTRY = Registry.get(Workbench.class);

  private Workbench workbench;
  private Set<String> layouts = new HashSet<String>();

  
  public Object createPlugin(Workbench workbench) {

    this.workbench = workbench;
    workbench.addWorkbenchListener(this);

    
    File dir = dir();
    if (!dir.isDirectory() && !dir.mkdirs())
      LOG.warning("No layout directory "+dir);
    else
      for (File layout : dir.listFiles()) { 
        String name = layout.getName();
        if (name.endsWith(".xml"))
          layouts.add(name.substring(0, name.length()-4));
      }
    
    
    layouts.add(LAYOUT_RESEARCH);
    layouts.add(LAYOUT_PRESENTATION);
    
    
    load(REGISTRY.get("layout", LAYOUT_RESEARCH));

    
    return this;
  }
  
  @Override
  public void createActions(Context context, Purpose purpose, Group into) {
    if (purpose!=Purpose.MENU)
      return;
    
    Action2.Group views = new ActionProvider.ViewActionGroup();
    into.add(views);
    
    Action2.Group layout = new Action2.Group(RES.getString("layout"), IMG);
    views.add(layout);
    
    for (String l : layouts)
      layout.add(new Switch(l));
    
    layout.add(new ActionProvider.SeparatorAction());
    layout.add(new New());
    layout.add(new Del());
    
  }

  @Override
  public void workbenchClosing(Workbench workbench) {
    save();
  }
  
  private File dir() {
    return new File(EnvironmentChecker.getProperty("user.home.genj/layouts", ".", "looking for layouts"));  
  }
  
  private void save() {
    String layout = REGISTRY.get("layout", (String)null);
    if (layout==null)
      return;
    
    File file = new File(dir(), layout+".xml");
    try {
      OutputStream out = new FileOutputStream(file);
      workbench.saveLayout(new OutputStreamWriter(out));
      out.close();
      LOG.log(Level.FINE, "Wrote layout "+layout+" into "+file);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "Couldn't write layout "+layout+" into "+file, t);
    }
  }
  
  private boolean load(String layout) {
    
    File file = new File(dir(), layout+".xml");
    try {
      InputStream in;
      String info;
      if (file.exists()) {
        in = new FileInputStream(file);
        info = "Read layout "+layout+" from "+file;
      } else {
        in = getClass().getResourceAsStream(layout+".xml");
        info = "Read pre-defined layout "+layout;
      }
      workbench.loadLayout(new InputStreamReader(in));
      in.close();
      LOG.log(Level.INFO, info);
      
      REGISTRY.put("layout", layout);
      
      return true;
      
    } catch (Throwable t) {
      
      REGISTRY.put("layout", (String)null);
      
      LOG.log(Level.WARNING, "Couldn't read layout for key "+layout+" from "+file, t);
    }

    return false;
  }
  
  private String name(String layout) {
    return RES.getString("layout."+layout, false);
  }

  
  private class Switch extends Action2 {
    private String layout;
    public Switch(String layout) {
      this.layout = layout;
      String txt = name(layout);
      setText(txt!=null ? txt : layout);
      if (layout.equals(REGISTRY.get("layout", "")))
        setSelected(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      save();
      load(layout);
    }
  } 

  
  private class New extends Action2 {
    public New() {
      setText(RES.getString("layout.new"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      String layout = DialogHelper.openDialog(getText(), DialogHelper.QUESTION_MESSAGE, RES.getString("layout.name"), "", DialogHelper.getComponent(e));
      if (layout==null||layouts.contains(layout))
        return;

      layouts.add(layout);
      REGISTRY.put("layout", layout);
    }
  } 

  
  private class Del extends Action2 {
    private List<String> choices;
    public Del() {
      setText(RES.getString("layout.del"));
      
      choices = new ArrayList<String>(layouts);
      for (ListIterator<String> li=choices.listIterator(); li.hasNext(); ) {
        if (name(li.next())!=null)
          li.remove();
      }
      Collections.sort(choices);
      
      setEnabled(!choices.isEmpty());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      
      String layout = DialogHelper.openDialog(getText(), DialogHelper.QUESTION_MESSAGE, RES.getString("layout.name"), choices, DialogHelper.getComponent(e));
      if (layout==null)
        return;
      
      File file = new File(dir(), layout+".xml");
      if (file.exists() && !file.delete())
        return;

      layouts.remove(layout);
      
      if (layout.equals(REGISTRY.get("layout", (String)null))) 
        load(LAYOUT_RESEARCH);
        
    }
  }

}
