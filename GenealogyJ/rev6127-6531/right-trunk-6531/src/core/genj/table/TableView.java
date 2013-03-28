
package genj.table;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.view.SettingsAction;
import genj.view.ToolBar;
import genj.view.View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;


public class TableView extends View {
  
  private final static Logger LOG = Logger.getLogger("genj.table");
  private final static Registry REGISTRY = Registry.get(TableView.class);
  
  
  private Resources resources = Resources.get(this);
  
  
   PropertyTableWidget propertyTable;
  
  
  private Map<String, Mode> modes = new HashMap<String, Mode>();
    {
      modes.put(Gedcom.INDI, new Mode(Gedcom.INDI, new String[]{"INDI","INDI:NAME","INDI:SEX","INDI:BIRT:DATE","INDI:BIRT:PLAC","INDI:OCCU", "INDI:FAMS", "INDI:FAMC"}));
      modes.put(Gedcom.FAM , new Mode(Gedcom.FAM , new String[]{"FAM" ,"FAM:MARR:DATE","FAM:MARR:PLAC", "FAM:HUSB", "FAM:WIFE", "FAM:CHIL" }));
      modes.put(Gedcom.OBJE, new Mode(Gedcom.OBJE, new String[]{"OBJE","OBJE:FILE:TITL"}));
      modes.put(Gedcom.NOTE, new Mode(Gedcom.NOTE, new String[]{"NOTE","NOTE:NOTE"}));
      modes.put(Gedcom.SOUR, new Mode(Gedcom.SOUR, new String[]{"SOUR","SOUR:TITL", "SOUR:TEXT"}));
      modes.put(Gedcom.SUBM, new Mode(Gedcom.SUBM, new String[]{"SUBM","SUBM:NAME" }));
      modes.put(Gedcom.REPO, new Mode(Gedcom.REPO, new String[]{"REPO","REPO:NAME", "REPO:NOTE"}));
    };
    
  
  private Mode currentMode;
  
  
  public TableView() {
    
    
    for (Mode mode : modes.values())
      mode.load();

    
    propertyTable = new PropertyTableWidget();
    propertyTable.setAutoResize(false);

    
    setLayout(new BorderLayout());
    add(propertyTable, BorderLayout.CENTER);
    
    
    currentMode = getMode(Gedcom.INDI);
    String tag = REGISTRY.get("mode", "");
    if (modes.containsKey(tag))
      currentMode = getMode(tag);
    
    
    new NextMode(true).install(this, "ctrl pressed LEFT");
    new NextMode(false).install(this, "ctrl pressed RIGHT");
    
    
  }
  
  public Gedcom getGedcom() {
    PropertyTableModel model = propertyTable.getModel();
    return model!=null ? model.getGedcom() : null;
  }
  
   PropertyTableWidget getTable() {
    return propertyTable;
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(480,320);
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
    
    REGISTRY.put("mode", set.getTag());
    
    PropertyTableModel currentModel = propertyTable.getModel();
    
    
    if (currentModel!=null&&currentMode!=null)
      currentMode.save();
    
    
    currentMode = set;
    
    
    if (currentModel!=null) {
      propertyTable.setModel(new Model(currentModel.getGedcom(),currentMode));
      propertyTable.setColumnLayout(currentMode.layout);
    }
  }
  
  @Override
  public void setContext(Context context, boolean isActionPerformed) {
    
    
    currentMode.save();

    
    PropertyTableModel old = propertyTable.getModel();
    if (context.getGedcom()==null) {
      if (old!=null)
        propertyTable.setModel(null);
      return;
    }
    
    
    if (old==null||old.getGedcom()!=context.getGedcom()) {
      propertyTable.setModel(new Model(context.getGedcom(), currentMode));
      propertyTable.setColumnLayout(currentMode.layout);
    }
    
    
    Mode mode = getModeFor(context);
    if (mode!=currentMode)
      mode.setSelected(true);

    
    propertyTable.select(context);
  }

  private Mode getModeFor(Context context) {
    
    for (Entity entity : context.getEntities()) {
      if (currentMode.tag.equals(entity.getTag()))
        return currentMode;
    }
    
    for (Entity entity : context.getEntities()) {
      Mode m = modes.get(entity.getTag());
      if (m!=null)
        return m;
    }
    
    return currentMode;
  }    
  
  
  public void populate(ToolBar toolbar) {
	  
    ButtonGroup group = new ButtonGroup();
    
    for (int i=0, j=1;i<Gedcom.ENTITIES.length;i++) {
      String tag = Gedcom.ENTITIES[i];
      Mode mode = getMode(tag);
      JToggleButton b = new JToggleButton(mode);
      toolbar.add(b);
      group.add(b);
      if (currentMode==mode)
        mode.setSelected(true);
    }
    
    toolbar.add(new Settings());

  }
  
  
  @Override
  public void removeNotify() {
    
    for (Mode mode : modes.values())
      mode.save();
    
    super.removeNotify();
  }
  
  
  private class Settings extends SettingsAction {

    @Override
    protected TableViewSettings getEditor() {
      return new TableViewSettings(TableView.this);
    }

  }
  
  
  private class NextMode extends Action2 {
    private int dir;
    private NextMode(boolean left) {
      if (left) {
        dir = -1;
      } else {
        dir = 1;
      }
    }
    public void actionPerformed(ActionEvent event) {
      int next = -1;
      for (int i=0,j=Gedcom.ENTITIES.length; i<j; i++) {
        next = (i+j+dir)%Gedcom.ENTITIES.length;
        if (currentMode == getMode(Gedcom.ENTITIES[i])) 
          break;
      }
      getMode(Gedcom.ENTITIES[next]).setSelected(true);
    }
  } 
  
  
  private class Model extends AbstractPropertyTableModel {

    
    private Mode mode;
    
    
    private List<Entity> rows;
    
    
    private Model(Gedcom gedcom, Mode set) {
      super(gedcom);
      mode = set;
    }
    
    
    public int getNumCols() {
      return mode.getPaths().length;
    }
    
    
    public int getNumRows() {
      
      if (rows==null) 
        rows = new ArrayList<Entity>(super.getGedcom().getEntities(mode.getTag()));
      
      return rows.size();
    }
    
    
    public TagPath getColPath(int col) {
      return mode.getPaths()[col];
    }

    
    public Property getRowRoot(int row) {
      
      
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
      invalidate(gedcom, property.getEntity(), added.getPath());
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

  
   class Mode extends Action2 {
    
    
    private String tag;
    private String[] defaults;
    private TagPath[] paths;
    private String layout;
    
    
    private Mode(String t, String[] d) {
      
      tag      = t;
      defaults = d;
      paths    = TagPath.toArray(defaults);

      
      setTip(resources.getString("mode.tip", Gedcom.getName(tag,true)));
      setImage(Gedcom.getEntityImage(tag));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      setSelected(true);
    }
    
    @Override
    public boolean setSelected(boolean selected) {
      if (selected) 
        setMode(this);
      return super.setSelected(selected);
    }
    
    
    private void load() {
      
      String[] ps = REGISTRY.get(tag+".paths" , (String[])null);
      if (ps!=null) 
        paths = TagPath.toArray(ps);

      layout = REGISTRY.get(tag+".layout", (String)null);
      
    }
    
    
     void setPaths(TagPath[] set) {
      paths = set;
      if (currentMode==this)
        setMode(currentMode);
    }
    
    
     TagPath[] getPaths() {
      return paths;
    }
    
    
    private void save() {
      
      
      if (currentMode==this && propertyTable.getModel()!=null) 
        layout = propertyTable.getColumnLayout();

	    REGISTRY.put(tag+".paths" , paths);
	    REGISTRY.put(tag+".layout", layout);
    }
    
    
     String getTag() {
      return tag;
    }
    
  } 
  
} 
