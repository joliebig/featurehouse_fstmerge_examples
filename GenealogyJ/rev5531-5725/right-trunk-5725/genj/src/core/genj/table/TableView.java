
package genj.table;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.view.ToolBarSupport;
import genj.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;


public class TableView extends JPanel implements ToolBarSupport  {
  
  private final static Logger LOG = Logger.getLogger("genj.table");

  
  private Resources resources = Resources.get(this);
  
  
   Gedcom gedcom;
  
  
  private ViewManager manager;
  
  
  private Registry registry;
  
  
  private String title;
  
  
   PropertyTableWidget propertyTable;
  
  
  private GedcomListener listener;
  
  
  private Map modes = new HashMap();
    {
      modes.put(Gedcom.INDI, new Mode(Gedcom.INDI, new String[]{"INDI","INDI:NAME","INDI:SEX","INDI:BIRT:DATE","INDI:BIRT:PLAC","INDI:FAMS", "INDI:FAMC", "INDI:OBJE:FILE"}));
      modes.put(Gedcom.FAM , new Mode(Gedcom.FAM , new String[]{"FAM" ,"FAM:MARR:DATE","FAM:MARR:PLAC", "FAM:HUSB", "FAM:WIFE", "FAM:CHIL" }));
      modes.put(Gedcom.OBJE, new Mode(Gedcom.OBJE, new String[]{"OBJE","OBJE:TITL"}));
      modes.put(Gedcom.NOTE, new Mode(Gedcom.NOTE, new String[]{"NOTE","NOTE:NOTE"}));
      modes.put(Gedcom.SOUR, new Mode(Gedcom.SOUR, new String[]{"SOUR","SOUR:TITL", "SOUR:TEXT"}));
      modes.put(Gedcom.SUBM, new Mode(Gedcom.SUBM, new String[]{"SUBM","SUBM:NAME" }));
      modes.put(Gedcom.REPO, new Mode(Gedcom.REPO, new String[]{"REPO","REPO:NAME", "REPO:NOTE"}));
    };
  
  
  private Mode currentMode = getMode(Gedcom.INDI);
  
  
  public TableView(String titl, Gedcom gedcom, Registry registry, ViewManager mgr) {
    
    
    this.gedcom = gedcom;
    this.registry = registry;
    this.title = titl;
    this.manager = mgr;
    
    
    loadProperties();
    
    
    propertyTable = new PropertyTableWidget(null);
    propertyTable.setAutoResize(false);

    
    setLayout(new BorderLayout());
    add(propertyTable, BorderLayout.CENTER);
    
    
    new NextMode(true).install(this, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new NextMode(false).install(this, JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    
  }
  
   TableModel getModel() {
    return propertyTable.getTableModel();
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(480,320);
  }
  
    
  public void addNotify() {
    
    super.addNotify();
    
    Mode set = currentMode;
    currentMode = null;
    setMode(set);
  }

  
  public void removeNotify() {
    
    saveProperties();
    
    super.removeNotify();
    
    propertyTable.setModel(null);
  }
  
  
   Mode getMode() {
    return currentMode;
  }
  
  
   Mode getMode(String tag) {
    
    Mode mode = (Mode)modes.get(tag); 
    if (mode==null) {
      mode = new Mode(tag, new String[0]);
      modes.put(tag, mode);
    }
    return mode;
  }
  
  
   void setMode(Mode set) {
    
    if (currentMode!=null)
      currentMode.save(registry);
    
    currentMode = set;
    
    propertyTable.setModel(new Model(currentMode));
    
    propertyTable.setColumnLayout(currentMode.layout);
  }
  
  
  public void populate(JToolBar bar) {
    
    ButtonHelper bh = new ButtonHelper().setInsets(0).setContainer(bar);
    
    InputMap inputs = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    for (int i=0, j=1;i<Gedcom.ENTITIES.length;i++) {
      String tag = Gedcom.ENTITIES[i];
      SwitchMode change = new SwitchMode(getMode(tag));
      bh.create(change);
    }
    
    
  }
  
  
  private void loadProperties() {

    
    Iterator it = modes.values().iterator();
    while (it.hasNext()) {
      Mode mode = (Mode)it.next();
      mode.load(registry);
    }

    
    String tag = registry.get("mode", "");
    if (modes.containsKey(tag))
      currentMode = getMode(tag);
    
    
  }
  
  
  private void saveProperties() {
    
    
    registry.put("mode", currentMode.getTag());
    
    
    Iterator it = modes.values().iterator();
    while (it.hasNext()) {
      Mode mode = (Mode)it.next();
      mode.save(registry);
    }
    
  }  
  
  
  private class NextMode extends Action2 {
    private int dir;
    private NextMode(boolean left) {
      int vk;
      if (left) {
        vk = KeyEvent.VK_LEFT;
        dir = -1;
      } else {
        vk = KeyEvent.VK_RIGHT;
        dir = 1;
      }
      setAccelerator(KeyStroke.getKeyStroke(vk, KeyEvent.CTRL_DOWN_MASK));
    }
    protected void execute() {
      int next = -1;
      for (int i=0,j=Gedcom.ENTITIES.length; i<j; i++) {
        next = (i+j+dir)%Gedcom.ENTITIES.length;
        if (currentMode == getMode(Gedcom.ENTITIES[i])) 
          break;
      }
      setMode(getMode(Gedcom.ENTITIES[next]));
    }
  } 
  
  
  private class SwitchMode extends Action2 {
    
    private Mode mode;
    
    SwitchMode(Mode mode) {
      this.mode = mode;
      setTip(resources.getString("mode.tip", Gedcom.getName(mode.getTag(),true)));
      setImage(Gedcom.getEntityImage(mode.getTag()));
    }
    
    public void execute() {
      setMode(mode);
    }
  } 
  
  
  private class Model extends AbstractPropertyTableModel {

    
    private Mode mode;
    
    
    private List rows;
    
    
    private Model(Mode set) {
      mode = set;
    }
    
    
    public Gedcom getGedcom() {
      return gedcom;
    }

    
    public int getNumCols() {
      return mode.getPaths().length;
    }
    
    
    public int getNumRows() {
      
      if (rows==null) 
        rows = new ArrayList(gedcom.getEntities(mode.getTag()));
      
      return rows.size();
    }
    
    
    public TagPath getPath(int col) {
      return mode.getPaths()[col];
    }

    
    public Property getProperty(int row) {
      
      
      getNumRows();

      
      Property result = (Property)rows.get(row);
      if (result==null)
        return result;
      
      
      if (result.getEntity()==null) {
        result = null;
        rows.set(row, null);
      }
      
      
      return result;
    }
    
    
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      
      if (!mode.getTag().equals(entity.getTag())) 
        return;
      
      rows.add(entity);
      
      fireRowsAdded(rows.size()-1, rows.size()-1);
      
    }

    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      
      if (!mode.getTag().equals(entity.getTag())) 
        return;
      
      for (int i=0;i<rows.size();i++) {
        if (rows.get(i)==entity) {
          rows.remove(i);
          
          fireRowsDeleted(i, i);
          
          return;
        }
      }
      
      LOG.warning("got notified that entity "+entity.getId()+" was deleted but it wasn't in rows in the first place");
    }

    
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      invalidate(gedcom, property.getEntity(), property.getPath());
    }

    
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      invalidate(gedcom, property.getEntity(), property.getPath());
    }

    
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
      invalidate(gedcom, property.getEntity(), new TagPath(property.getPath(), deleted.getTag()));
    }
    
    private void invalidate(Gedcom gedcom, Entity entity, TagPath path) {
      
      if (!mode.getTag().equals(entity.getTag())) 
        return;
      
      TagPath[] paths = mode.getPaths();
      for (int i=0;i<paths.length;i++) {
        if (paths[i].equals(path)) {
          for (int j=0;j<rows.size();j++) {
            if (rows.get(j)==entity) {
                fireRowsChanged(j,j,i);
                return;
            }
          }      
        }
      }
      
    }

  } 

  
   class Mode {
    
    
    private String tag;
    private String[] defaults;
    private TagPath[] paths;
    private String layout;
    
    
    private Mode(String t, String[] d) {
      
      tag      = t;
      defaults = d;
      paths    = TagPath.toArray(defaults);
    }
    
    
    private void load(Registry r) {
      
      String[] ps = r.get(tag+".paths" , (String[])null);
      if (ps!=null) 
        paths = TagPath.toArray(ps);

      layout = r.get(tag+".layout", (String)null);
      
    }
    
    
     void setPaths(TagPath[] set) {
      paths = set;
      if (currentMode==this)
        setMode(currentMode);
    }
    
    
     TagPath[] getPaths() {
      return paths;
    }
    
    
    private void save(Registry r) {
      
      
      if (currentMode==this) 
        layout = propertyTable.getColumnLayout();

	    registry.put(tag+".paths" , paths);
	    registry.put(tag+".layout", layout);
    }
    
    
     String getTag() {
      return tag;
    }
    
  } 
  
} 
